package com.capstone.TCP_Client;

import com.capstone.LoginActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;

public class RequestActions extends Thread {
	private Credentials credentials = new Credentials();
	private LinkedList<Request> queue = new LinkedList<Request>();

	private String token = "";
	private String response = "";

	private Socket socket;
	private OutputStream out;
	private BufferedReader in;

	private LoginActivity parent;

	public RequestActions(LoginActivity parent){
		this.parent = parent;
	}

	public void run(){
		try {
			while(true) {
				while (queue.isEmpty()) ;
				socket = new Socket(credentials.getHost(), credentials.getPort());
				out = socket.getOutputStream();
				in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
				out.write(queue.poll().getRequest().getBytes());
				out.flush();
				String line;
				while ((line = in.readLine()) != null) {
					response += line;
				}
				System.out.println(response);
//				response.replaceAll("\n")
				checkForToken(response);
				in.close();
				out.close();
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void populateDashboard(){
		queue.add(new POSTRequest(Paths.getEntriesPath(), "token="+token, credentials.getHost()));
	}

	public void login(String username, String password) {
		queue.add(new POSTRequest(Paths.getLoginPath(), "username="+username+"&password="+password, credentials.getHost()));
	}

	public void register(String username, String password) {
		queue.add(new POSTRequest(Paths.getRegisterPath(), "token="+token, credentials.getHost()));
	}

	public void sendImage(){
		queue.add(new POSTRequest(Paths.getClassifyPath(), "token="+token+"&", credentials.getHost()));
	}

	private void checkForToken(String input){
		if(input.contains("token")){
			token = input.substring(input.indexOf("Bearer")+7, input.length()-2);
//			System.out.println("*******TOKEN****" + token);
			parent.goToDashboard();
		} else {
			parent.invalidCredentials();
		}
	}



}
