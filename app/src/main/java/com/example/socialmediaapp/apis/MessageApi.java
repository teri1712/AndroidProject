package com.example.socialmediaapp.apis;

import com.example.socialmediaapp.apis.entities.MessageItemBody;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;

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
   Call<List<MessageItemBody>> loadMessages(@Path("chatId") Integer chatId, @Query("lastOrder") Integer lastOrder);
   @Multipart
   @POST("/message/text/{chatId}")
   Call<MessageItemBody> uploadTextMessage(@Part("content") RequestBody content, @Path("chatId") Integer chatId);

   @POST("/message/image/{chatId}")
   Call<MessageItemBody> uploadImageMessage(@Part MultipartBody.Part media_content, @Path("chatId") Integer chatId);

   @POST("/message/icon/{chatId}")
   Call<MessageItemBody> uploadIconMessage(@Path("chatId") Integer chatId);


}
