package com.workato.onprem.extensions.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpBackOffUnsuccessfulResponseHandler;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.workato.onprem.extensions.model.ExecutionResult;

public class ShellExecutionRunnable implements Runnable {
	public static File logsDirectory = new File("./shell-logs");
	
	private String fileLocation;
	private GenericUrl returnURL;
	private HttpTransport HTTP_TRANSPORT;
	private String executionId;
	private String[] params;
	
	public void run() {
		try {
			Runtime rt = Runtime.getRuntime();
			String[] baseCommand = new String[] { "/bin/sh", this.fileLocation };
			String[] fullCommand = (String[])ArrayUtils.addAll(baseCommand, params);
			Process pr = rt.exec(fullCommand);

			String result = shellLogger(pr.getInputStream());
			
			String bytesEncoded = Base64.encodeBase64String(result.toString().getBytes());
			ExecutionResult er = new ExecutionResult(executionId, bytesEncoded);
			sendHTTPResponse(er);
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
	
	private String shellLogger(InputStream in) throws IOException {
		// create directory to write logs to if it doesn't exist
		logsDirectory.mkdir();
		
		File fileInDirectory = new File(logsDirectory, String.format("%s.log", executionId));
		
		StringBuilder result = new StringBuilder();
		BufferedReader input = new BufferedReader(new InputStreamReader(in));
		String line = "";
		
		PrintWriter writer = new PrintWriter(fileInDirectory, "UTF-8");
		while ((line = input.readLine()) != null) {
			result.append(line + System.lineSeparator());
			writer.println(line);
			writer.flush();
		}
		writer.close();
		
		return result.toString();
	}

	public ShellExecutionRunnable(String executionId, String input, String returnURL, HttpTransport HTTP_TRANSPORT, String[] params) {
		super();
		this.fileLocation = input;
		this.returnURL = new GenericUrl(returnURL);
		this.HTTP_TRANSPORT = HTTP_TRANSPORT;
		this.executionId = executionId;
		this.params = params;
	}
	
	private void sendHTTPResponse(ExecutionResult er) throws IOException {
		HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
		JsonFactory jsonFactory = new JacksonFactory();
		HttpRequest request = requestFactory.buildPostRequest(returnURL, new JsonHttpContent(jsonFactory, er));
		request.getHeaders().setContentType("application/json");
		ExponentialBackOff backoff = new ExponentialBackOff.Builder().setInitialIntervalMillis(500)
				.setMaxElapsedTimeMillis(900000).setMaxIntervalMillis(6000).setMultiplier(1.5)
				.setRandomizationFactor(0.5).build();
		request.setUnsuccessfulResponseHandler(new HttpBackOffUnsuccessfulResponseHandler(backoff));
		request.execute();
	}
}
