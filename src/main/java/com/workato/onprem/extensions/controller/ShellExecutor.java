package com.workato.onprem.extensions.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.workato.onprem.extensions.dao.ExecutionDaoImpl;
import com.workato.onprem.extensions.model.Execution;
import com.workato.onprem.extensions.model.ExecutionState;

@Controller
public class ShellExecutor {

	@Inject
	private Environment env;
	private ExecutionDaoImpl executionDao = new ExecutionDaoImpl();
	private ObjectMapper mapper = new ObjectMapper();
	
	@RequestMapping(path = "/execution/{executionId}", method = RequestMethod.GET)
	public @ResponseBody String getExecutionStatus(@PathVariable(value = "executionId") String executionId)
			throws Exception {
		return mapper.writeValueAsString(executionDao.findById(executionId));
	}

	@RequestMapping(path = "/execution/{executionId}/stop", method = RequestMethod.GET)
	public @ResponseBody String stopExecution(@PathVariable(value = "executionId") String executionId)
			throws Exception {
		executionDao.stopExecution(executionId);
		return mapper.writeValueAsString(executionDao.findById(executionId));
	}

	@RequestMapping(path = "/execution/{executionId}/start", method = RequestMethod.GET)
	public @ResponseBody String startExecution(@PathVariable(value = "executionId") String executionId)
			throws Exception {
		executionDao.startExecution(executionId);
		return mapper.writeValueAsString(executionDao.findById(executionId));
	}

	@RequestMapping(path = "/execution/create", method = RequestMethod.POST)
	public @ResponseBody String createExecution(@RequestBody Map<String, Object> input) throws Exception {
		long currentTime = System.currentTimeMillis();
		executionDao.createExecution(new Execution(String.valueOf(currentTime), null, currentTime, 0, 0,
				ExecutionState.NEW, currentTime, currentTime, (String) input.get("returnUrl")));
		executionDao.startExecution(String.valueOf(currentTime));
		return mapper.writeValueAsString(executionDao.findById(String.valueOf(currentTime)));
	}

	@RequestMapping(path = "/execution/list", method = RequestMethod.GET)
	public @ResponseBody String listExecutions(@RequestParam Map<String, Object> input) throws Exception {
		return mapper.writeValueAsString(executionDao.findAll());
	}

	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody Map<String, Object> startExecution(@RequestBody Map<String, Object> input) throws Exception {
		// Validate file_location
		if (input.get("file_location") == null || input.get("file_location").toString() == "") {
			Map<String, Object> invalidCmd = new HashMap<String, Object>();
			invalidCmd.put("status", "failure");
			invalidCmd.put("error", "file_location is not specified");
			return invalidCmd;
		}
		
		// Validate params
		String[] params = new String[] {};
		if (input.get("params") != null) {
			if (input.get("params") instanceof String) {
				params = new String[] {(String) input.get("params")};
			} else if (input.get("params") instanceof ArrayList) {
				@SuppressWarnings("unchecked")
				ArrayList<String> p = (ArrayList<String>) input.get("params");
				params = p.toArray(new String[0]);
			}
		}
		return executeShell((String) input.get("file_location"), (String) input.get("return_url"), params);
	}

	public Map<String, Object> executeShell(String file_location, String return_url, String[] params) throws Exception {
		String executionId = String.valueOf(System.currentTimeMillis());
		ShellExecutionRunnable r = new ShellExecutionRunnable(executionId, file_location, return_url,
				new NetHttpTransport(), params);
		Thread t = new Thread(r);
		t.start();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("status", "success");
		map.put("execution_id", executionId);

		return map;
	}
}
