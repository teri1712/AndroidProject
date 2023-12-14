package com.example.socialmediaapp.apis;

import com.example.socialmediaapp.apis.entities.ChatBody;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatApi {
   @PUT("/chat/{firebase-token}")
   Call<List<ChatBody>> initTokenAndPullChat(@Path("firebase-token") String fbToken);


   @GET("/chat/{chatId}")
   Call<ChatBody> loadChat(@Path("chatId") Integer chatId);
}
