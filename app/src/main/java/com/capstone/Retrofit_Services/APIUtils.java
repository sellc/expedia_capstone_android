package com.capstone.Retrofit_Services;

public class APIUtils {

    private APIUtils(){

    }

    public static final String API_URL="http://ec2-34-220-221-99.us-west-2.compute.amazonaws.com:3001/api/v1/";

    public static FileService getFileService(){
        return RetrofitClient.getClient(API_URL).create(FileService.class);
    }
}
