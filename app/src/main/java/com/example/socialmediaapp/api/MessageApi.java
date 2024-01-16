package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.MessageItemBody;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageApi {
   @GET("/message/{chatId}")
   Call<List<MessageItemBody>> loadMessages(@Path("chatId") String chatId, @Query("lastOrder") Integer lastOrder);

   @Multipart
   @POST("/message/text/{chatId}")
   Call<MessageItemBody> uploadMessage(@Part("content") RequestBody content, @Path("chatId") String chatId, @Query("time") Long time);

   @Multipart
   @POST("/message/image/{chatId}")
   Call<MessageItemBody> uploadImage(@Part MultipartBody.Part media_content, @Path("chatId") String chatId, @Query("time") Long time);

   @POST("/message/icon/{chatId}")
   Call<MessageItemBody> uploadIcon(@Path("chatId") String chatId, @Query("time") Long time);


}
