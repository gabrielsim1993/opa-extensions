package com.workato.onprem.extensions.model;

import com.google.api.client.util.Key;

public class ExecutionResult {

	@Key
	public String id;

	@Key
	public String result;

	public ExecutionResult(String id, String result) {
		super();
		this.id = id;
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

}
