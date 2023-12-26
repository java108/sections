package com.natlex.assignment;

public class ExportStatus {
	private Status status;
	private String path;
	public ExportStatus(Status status, String path) {
		super();
		this.status = status;
		this.path = path;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	

}
