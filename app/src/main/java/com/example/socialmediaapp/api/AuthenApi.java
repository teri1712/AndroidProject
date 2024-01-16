package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.PrincipalBody;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthenApi {

    @GET("/user/principal")
    Call<PrincipalBody> loadPrincipal(@Header("Authorization") String fbToken);

    @POST("/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);
    @POST("/logout")
    Call<ResponseBody> logout();
    @POST("/signup")
    @FormUrlEncoded
    Call<ResponseBody> signup(@Field("username") String username, @Field("password") String password);
}
