package com.capstone.TCP_Client;

public class POSTRequest extends Request {

	public POSTRequest(String path, String data, String host) {
		super(path, data, host);
	}

	//Create a POST request with the specified header information
	public void createRequest() {
		request = "POST " + path + " HTTP/1.1\r\n" + "Accept: */*\r\n" + "Host: " + host + "\r\n"
				+ "Content-Type: application/x-www-form-urlencoded\r\n" + "Content-Length: " + data.length()
				+ "\r\n\r\n" + data;
	}

}
