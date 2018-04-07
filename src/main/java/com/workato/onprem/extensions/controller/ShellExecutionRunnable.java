package com.workato.onprem.extensions.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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

			StringBuilder result = new StringBuilder();
			BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = "";
			while ((line = input.readLine()) != null) {
				result.append(System.lineSeparator() + line);
			}
			String bytesEncoded = Base64.encodeBase64String(result.toString().getBytes());
			ExecutionResult er = new ExecutionResult(executionId, bytesEncoded);
			sendHTTPResponse(er);
		} catch (Exception e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
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
