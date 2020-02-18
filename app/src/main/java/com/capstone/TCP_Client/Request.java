package com.capstone.TCP_Client;

public abstract class Request {

	public String path, data, host, request;

	//Constructor
	public Request(String path, String data, String host) {
		this.path = path;
		this.data = data;
		this.host = host;
		request = "";
		createRequest();
	}

	//This must be implemented by the request type
	public abstract void createRequest();

	//Get the full request
	public String getRequest() {
		return request;
	}

}
