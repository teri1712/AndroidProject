package com.example.socialmediaapp.apis;

import com.example.socialmediaapp.apis.entities.CommentBody;
import com.example.socialmediaapp.apis.entities.CommentDataSyncBody;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.apis.entities.PostDataSyncBody;

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

public interface PostApi {


    @GET("/post/get")
    Call<PostBody> get(@Path("id") int id);

    @Multipart
    @POST("/post/new")
    Call<PostBody> upload(@Part("status") RequestBody status, @Part("media_type") RequestBody type,
                          @Part MultipartBody.Part media_content);

    @GET("/post/fetch/{countRead}")
    Call<List<PostBody>> fetchPost(@Path("countRead") int countRead);

    @GET("/post/load/{alias}")
    Call<List<PostBody>> loadPostOfUser(@Path("alias") String alias);

    @GET("/post/comment/{postId}")
    Call<List<CommentBody>> fetchComments(@Path("postId") int id);

    @Multipart
    @POST("/post/comment/send/{postId}")
    Call<CommentBody> uploadComment(@Path("postId") Integer postId, @Part("content") RequestBody content,
                                    @Part MultipartBody.Part media_content);

    @PUT("/post/like/{postId}")
    Call<ResponseBody> likePost(@Path("postId") Integer postId);

    @PUT("/post/unlike/{postId}")
    Call<ResponseBody> unlikePost(@Path("postId") Integer postId);

    @GET("/post/sync/{postId}")
    Call<PostDataSyncBody> syncPostData(@Path("postId") Integer postId);

    @GET("/post/comment/sync/{commentId}")
    Call<CommentDataSyncBody> syncCommenData(@Path("commentId") Integer commentId);

    @PUT("/post/comment/like/{commentId}")
    Call<ResponseBody> likeComment(@Path("commentId") Integer commentId);

    @PUT("/post/comment/unlike/{commentId}")
    Call<ResponseBody> unlikeComment(@Path("commentId") Integer commentId);


    @PUT("/post/replyComment/like/{replyCommentId}")
    Call<ResponseBody> userLikeReplyComment(@Path("replyCommentId") Integer replyCommentId);

    @PUT("/post/replyComment/unlike/{replyCommentId}")
    Call<ResponseBody> userUnlikeReplyComment(@Path("replyCommentId") Integer replyCommentId);

}
