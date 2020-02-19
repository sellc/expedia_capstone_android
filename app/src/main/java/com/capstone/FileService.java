package com.capstone;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface FileService {

    @Multipart
    @POST("classify")
    Call<FileInfo> upload(@Part MultipartBody.Part file);

}
