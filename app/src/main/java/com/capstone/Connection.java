package com.capstone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection extends Thread{

    private static final int port = 0;
    private static final String address = "0.0.0.0";
    private Socket s;
    private PrintWriter pw;
    private BufferedReader br;
    private static boolean newServerResponse;
    private static String serverResponse;
    private static String userInput;

    public Connection (){
        userInput = "";
        serverResponse = "";
        newServerResponse = false;
    }

    public void run(){
        try {
            while (true) {
                if (!userInput.equals("")) {
                    if(establishConnection()) {
                        sendMessage();
//                        listenForResponse();
//                        serverResponse = br.readLine();
//                        newServerResponse = true;

                        pw.close();
                        br.close();
                        s.close();
                    }
                }
            }
        } catch (IOException e){

        }
    }

    private boolean establishConnection(){
        try {
            s = new Socket(address, port);
            if(s.isConnected()){
                pw = new PrintWriter(s.getOutputStream(), true);
                br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            }
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    private void sendMessage(){
        if (s.isConnected() && pw != null) {
            pw.println(userInput);
            userInput = "";
        }
    }

//    private void listenForResponse(){
//        try {
////            while (!hasNewResponse()) {
////            }
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    public static void setUserInput(String input){
        userInput = input;
    }

    public static boolean hasNewResponse(){
        return newServerResponse;
    }

    public static String getServerResponse(){
        newServerResponse = false;
        return serverResponse;
    }

}
