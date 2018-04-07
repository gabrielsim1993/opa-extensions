package com.workato.onprem.extensions.model;

public class Execution {
	private String id;
	private String result;
	private long timeStarted;
	private long timeEnded;
	private long createdOn;
	private long modifiedOn;
	private int exitCode;
	private ExecutionState status;
	private String returnUrl;

	public Execution(String id, String result, long timeStarted, long timeEnded, int exitCode, ExecutionState status,
			long createdOn, long modifiedOn, String returnUrl) {
		super();
		this.id = id;
		this.result = result;
		this.timeStarted = timeStarted;
		this.timeEnded = timeEnded;
		this.exitCode = exitCode;
		this.status = status;
		this.createdOn = createdOn;
		this.modifiedOn = modifiedOn;
		this.setReturnUrl(returnUrl);
	}

	public long getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
	}

	public long getModifiedOn() {
		return modifiedOn;
	}

	public void setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
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

	public long getTimeStarted() {
		return timeStarted;
	}

	public void setTimeStarted(long timeStarted) {
		this.timeStarted = timeStarted;
	}

	public long getTimeEnded() {
		return timeEnded;
	}

	public void setTimeEnded(long timeEnded) {
		this.timeEnded = timeEnded;
	}

	public int getExitCode() {
		return exitCode;
	}

	public void setExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	public ExecutionState getStatus() {
		return status;
	}

	public void setStatus(ExecutionState status) {
		this.status = status;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

}
