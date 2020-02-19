package com.capstone;

public class APIUtils {

    private APIUtils(){

    }

    public static final String API_URL="";

    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}
