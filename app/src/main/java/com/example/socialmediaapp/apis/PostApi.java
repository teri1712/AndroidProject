package com.example.socialmediaapp.apis;

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
import retrofit2.http.Query;

public interface PostApi {

    @Multipart
    @POST("/post")
    Call<PostBody> upload(@Part("status") RequestBody status, @Part("media_type") RequestBody type,
                          @Part MultipartBody.Part media_content);

    @GET("/post")
    Call<List<PostBody>> load();

    @PUT("/post/like/{postId}")
    Call<ResponseBody> likePost(@Path("postId") Integer postId);

    @PUT("/post/unlike/{postId}")
    Call<ResponseBody> unlikePost(@Path("postId") Integer postId);

    @GET("/post/sync/{postId}")
    Call<PostDataSyncBody> syncPostData(@Path("postId") Integer postId);
    @GET("/post/{alias}")
    Call<List<PostBody>> loadPostsOfUser(@Path("alias") String alias, @Query("lastId") Integer lastId);



}
