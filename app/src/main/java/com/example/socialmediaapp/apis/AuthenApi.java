package com.example.socialmediaapp.apis;

import com.example.socialmediaapp.apis.entities.HomeEntranceBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface AuthenApi {

    @POST("/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);
    @POST("/signup")
    @FormUrlEncoded
    Call<ResponseBody> signup(@Field("username") String username, @Field("password") String password);
}
