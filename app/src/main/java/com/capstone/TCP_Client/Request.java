package com.capstone.TCP_Client;

public abstract class Request {

	public String path, data, host, request;

	public Request(String path, String data, String host) {
		this.path = path;
		this.data = data;
		this.host = host;
		request = "";
		createRequest();
	}

	public abstract void createRequest();

	public String getRequest() {
		return request;
	}

}
