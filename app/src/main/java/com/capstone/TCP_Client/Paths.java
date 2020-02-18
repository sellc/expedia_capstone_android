package com.capstone.TCP_Client;

public class Paths {

    // This file stores all paths and establish
    // one central location for all path information.

    public static String getBase() { return "/api/v1/"; }

    public static String getEntriesPath() {
        return getBase();
    }

    public static String getLoginPath(){
        return getBase() + "users/login";
    }

    public static String getRegisterPath(){
        return getBase() + "users/register";
    }

    public static String getClassifyPath() { return getBase() + "classify"; }

}
