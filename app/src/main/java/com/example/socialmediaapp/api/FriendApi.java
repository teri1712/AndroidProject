package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.FriendRequestBody;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface FriendApi {
   @GET("/user/friend/request")
   Call<List<FriendRequestBody>> loadFriendRequests(@Query("lastId") String lastId);
   @PUT("/user/friend/add/{id}")
   Call<ResponseBody> sendFriendRequest(@Path("id") String id);

   @PUT("/user/friend/cancel/{id}")
   Call<ResponseBody> cancelFriendRequest(@Path("id") String id);

   @PUT("/user/friend/accept/{id}")
   Call<ResponseBody> acceptFriendRequest(@Path("id") String id);

   @PUT("/user/friend/reject/{id}")
   Call<ResponseBody> rejectFriendRequest(@Path("id") String id);
}
