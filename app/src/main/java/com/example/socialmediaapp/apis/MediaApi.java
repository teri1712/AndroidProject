package com.example.socialmediaapp.apis;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface MediaApi {

    @GET("/media/image/{id}")
    Call<ResponseBody> loadImage(@Path("id") int id);

}
