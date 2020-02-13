package com.capstone.TCP_Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class RequestActions extends Thread {

	private LinkedList<Request> queue = new LinkedList<Request>();

	private String response = "";
	private boolean done = false;

	public void run(){
		while(true) {
			if (!queue.isEmpty()) {
				try {
					Socket socket = new Socket(Credentials.getHost(), Credentials.getPort());
					OutputStream out = socket.getOutputStream();
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
					out.write(queue.poll().getRequest().getBytes());
					out.flush();
					done = false;
					String line;
					response="";
					while ((line = in.readLine()) != null) {
						response += line;
					}
					done = true;
					in.close();
					out.close();
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void addPOSTToQueue(String path, String data){
		queue.add(new POSTRequest(path, data, Credentials.getHost()));
	}

	public void addGETToQueue(String path, String data){
		queue.add(new GETRequest(path, data, Credentials.getHost()));
	}

	public String getResponse(){
		String tempResponse = "";
		while(!done);
		tempResponse = response;
		response = "";
		done = false;
		return tempResponse;
	}

}
