package com.capstone.TCP_Client;

public class Credentials {

    static String token;

    // Hostname - 192.168.0.1 or (localhost)
    public static String getHost(){
        return "";
    }

    // Port - 80 & 443 (common)
    public static int getPort(){
        return 0;
    }

    // Set the session token
    public static void setToken(String newToken){
        token = newToken;
    }

}
