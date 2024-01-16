package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.NotificationBody;
import com.example.socialmediaapp.api.entities.NotificationPreset;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NotificationApi {

   @GET("/user/notification/preset")
   Call<NotificationPreset> loadPreset();
   @GET("/user/notification")
   Call<List<NotificationBody>> loadNotification(@Query("lastId") String lastId);

   @PUT("/user/notification/read/{readId}")
   Call<ResponseBody> readNotification(@Path("readId") String readId);
}
