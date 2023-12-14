package com.example.socialmediaapp.apis;

import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.apis.entities.CommentDataSyncBody;
import com.example.socialmediaapp.apis.entities.ReplyCommentBody;
import com.example.socialmediaapp.apis.entities.ReplyCommentDataSyncBody;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CommentApi {
   @GET("/comment/{postId}")
   Call<List<CommentBody>> loadComment(@Path("postId") int id, @Query("lastId") Integer lastId);

   @Multipart
   @POST("/comment/{postId}")
   Call<CommentBody> upload(@Path("postId") Integer postId, @Part("content") RequestBody content, @Part MultipartBody.Part media_content);

   @PUT("/comment/like/{commentId}")
   Call<ResponseBody> likeComment(@Path("commentId") Integer commentId);

   @PUT("/comment/unlike/{commentId}")
   Call<ResponseBody> unlikeComment(@Path("commentId") Integer commentId);

   @GET("/comment/sync/{commentId}")
   Call<CommentDataSyncBody> syncCommentData(@Path("commentId") Integer commentId);


   @GET("/comment/reply-comment/{commentId}")
   Call<List<ReplyCommentBody>> loadReplyComment(@Path("commentId") int id, @Query("lastId") Integer lastId);

   @Multipart
   @POST("/comment/reply-comment/{commentId}")
   Call<ReplyCommentBody> uploadReplyComment(@Path("commentId") Integer id, @Part("content") RequestBody content, @Part MultipartBody.Part media_content);

   @GET("/comment/reply-comment/sync/{commentId}")
   Call<ReplyCommentDataSyncBody> syncReplyCommentData(@Path("commentId") Integer commentId);

   @PUT("/comment/reply-comment/like/{commentId}")
   Call<ResponseBody> likeReplyComment(@Path("commentId") Integer id);

   @PUT("/comment/reply-comment/unlike/{commentId}")
   Call<ResponseBody> unlikeReplyComment(@Path("commentId") Integer id);

}
