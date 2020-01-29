package com.capstone;

public class APIUtils {

    private APIUtils(){

    }

    public static final String API_URL="http://192.168.1.10:50000/";

    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}
