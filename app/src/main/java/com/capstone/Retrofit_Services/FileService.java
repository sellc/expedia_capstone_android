package com.capstone.Retrofit_Services;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileService {

    @Multipart
    @POST("classify")
    Call<List<Result>> upload(@Part MultipartBody.Part file);

    @Multipart
    @GET("classify")
    Call<List<Result>> get();
}
