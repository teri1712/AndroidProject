package com.example.socialmediaapp.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MediaApi {
    @GET("/media/image/{id}")
    Call<ResponseBody> loadImage(@Path("id") String id);

}
