package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.ChatBody;
import com.example.socialmediaapp.api.entities.ChatDetailsBody;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatApi {
   @PUT("/chat/init")
   Call<List<ChatInfo>> initTokenAndPullChat();

   @GET("/chat/{chatId}")
   Call<ChatBody> loadChat(@Path("chatId") String chatId);

   @PUT("/chat/seen/{chatId}")
   Call<ResponseBody> seenMessage(@Path("chatId") String chatId, @Query("time") Long time);

   @GET("/chat/details/{id}")
   Call<ChatDetailsBody> loadChatDetails(@Path("id") String id);
}
