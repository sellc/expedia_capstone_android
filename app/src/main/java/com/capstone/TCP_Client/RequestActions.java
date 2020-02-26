package com.capstone.TCP_Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class RequestActions extends Thread {

	private ConcurrentLinkedQueue<Request> queue = new ConcurrentLinkedQueue<>();

	private volatile String response = "";
	private volatile boolean done = false;

	public void run(){
		Socket socket;
		OutputStream out;
		BufferedReader in;
		while(true) {
			if (!queue.isEmpty()) {
				try {
					socket = new Socket(Credentials.getHost(), Credentials.getPort());
					out = socket.getOutputStream();
					in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
					out.write(queue.poll().getRequest().getBytes());
					out.flush();
					done = false;
					String line;
					response = "";
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

	//Add a POST request to the queue
	public void addPOSTToQueue(String path, String data){
		queue.add(new POSTRequest(path, data, Credentials.getHost()));
	}

	//Add a GET request to the queue
	public void addGETToQueue(String path, String data){
		queue.add(new GETRequest(path, data, Credentials.getHost()));
	}

	//Return the current response if it's available
	//****************************************
	//This should be changed to return		*
	//the requested response otherwise		*
	//this can be overwritten.				*
	//****************************************
	public String getResponse(){
		while(!done);
		String tempResponse = response;
		response = "";
		done = false;
		return tempResponse;
	}

}
