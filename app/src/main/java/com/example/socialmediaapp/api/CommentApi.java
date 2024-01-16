package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.api.entities.CommentDataSyncBody;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.api.entities.ReplyCommentBody;
import com.example.socialmediaapp.api.entities.ReplyCommentDataSyncBody;
import com.example.socialmediaapp.api.entities.requests.CommentAccessSyncBody;

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


   @GET("/comment/download/{commentId}")
   Call<CommentBody> download(@Path("commentId") String commentId);

   @GET("/comment/{postId}")
   Call<List<CommentBody>> loadComment(@Path("postId") String postId, @Query("lastId") String lastId);
   @Multipart
   @POST("/comment/{postId}")
   Call<CommentBody> upload(@Path("postId") String postId, @Part("content") RequestBody content, @Part MultipartBody.Part media_content);

   @PUT("/comment/like/{commentId}")
   Call<ResponseBody> likeComment(@Path("commentId") String commentId);

   @PUT("/comment/unlike/{commentId}")
   Call<ResponseBody> unlikeComment(@Path("commentId") String commentId);

   @GET("/comment/sync/{commentId}")
   Call<CommentDataSyncBody> syncCommentData(@Path("commentId") String commentId);

   @GET("/comment/list-reply/sync/{commentId}")
   Call<CommentAccessSyncBody> syncCommentAccess(@Path("commentId") String commentId);

   @GET("/comment/reply-comment/download/{replyCommentId}")
   Call<ReplyCommentBody> downloadReplyComment(@Path("replyCommentId") String replyCommentId);

   @GET("/comment/reply-comment/{commentId}")
   Call<List<ReplyCommentBody>> loadReplyComment(@Path("commentId") String id, @Query("lastId") String lastId);
   @Multipart
   @POST("/comment/reply-comment/{commentId}")
   Call<ReplyCommentBody> uploadReplyComment(@Path("commentId") String id, @Part("content") RequestBody content, @Part MultipartBody.Part media_content);

   @GET("/comment/reply-comment/sync/{commentId}")
   Call<ReplyCommentDataSyncBody> syncReplyCommentData(@Path("commentId") String commentId);

   @PUT("/comment/reply-comment/like/{commentId}")
   Call<ResponseBody> likeReplyComment(@Path("commentId") String id);

   @PUT("/comment/reply-comment/unlike/{commentId}")
   Call<ResponseBody> unlikeReplyComment(@Path("commentId") String id);

}
