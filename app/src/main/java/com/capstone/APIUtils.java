package com.capstone;

public class APIUtils {

    private APIUtils(){

    }

    public static final String API_URL="http://ec2-54-202-80-154.us-west-2.compute.amazonaws.com:3001";

    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}
