package com.capstone.Retrofit_Services;

public class APIUtils {
    private static final String API_URL="http://ec2-34-210-193-105.us-west-2.compute.amazonaws.com:3001/api/v1/";
    private static final FileService fileService = RetrofitClient.getClient(API_URL).create(FileService.class);

    public static FileService getFileService() {
        return fileService;
    }
}
