package com.scada.smartfarm.api;

import com.scada.smartfarm.response.ResponseFarms;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface APIInterface {

    @Multipart
    @POST("/maskImage")
    Call<ResponseFarms> callResponseFarm(@Part MultipartBody.Part parts, @Part("min_temp") RequestBody requestBody2, @Part("max_temp") RequestBody requestBody3);

}
