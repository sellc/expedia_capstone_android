package com.capstone;

import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class Connection extends Thread {

    private final int port = 35000;
    private final SocketAddress address = new InetSocketAddress("192.168.1.4", port);
    private Socket s;
    private static PrintWriter pw;
    private BufferedReader br;
    private boolean newServerResponse;
    private String serverResponse;

    public Connection(){
        try {
            s = new Socket();
            while(!s.isConnected()) {
                s.connect(address, 5000);
            }
            pw = new PrintWriter(s.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(!s.isClosed()){
            if(!serverResponse.equals("")){
                newServerResponse = true;
            }
        }
    }

    public static void sendMessage(String message){
        pw.println(message);
    }

    public static boolean checkForNewResponse(){
//        if(newServerResponse){
//
//        }
        return false;
    }

}
