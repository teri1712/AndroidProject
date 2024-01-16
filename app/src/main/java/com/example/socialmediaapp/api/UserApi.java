package com.example.socialmediaapp.api;

import com.example.socialmediaapp.api.entities.FriendRequestBody;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.api.entities.UserProfileBody;
import com.example.socialmediaapp.api.entities.UserSessionBody;
import com.example.socialmediaapp.api.entities.requests.UpdateUserRequestBody;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

  @GET("/user/info")
  Call<UserBasicInfoBody> loadUserBasicInfo(@Query("id") String id);

  @GET("/user/profile/{id}")
  Call<UserProfileBody> loadUserProfile(@Path("id") String id);

  @Multipart
  @PUT("/user/avatar")
  Call<PostBody> changeAvatar(@Part("status") RequestBody status, @Part MultipartBody.Part avatar);

  @Multipart
  @PUT("/user/background")
  Call<PostBody> changeBackground(@Part("status") RequestBody status, @Part MultipartBody.Part background);

  @PUT("/user/info")
  Call<ResponseBody> changeInfo(@Body UpdateUserRequestBody changes);

  @GET("/user/homepage")
  Call<UserSessionBody> loadUserSession();

  @Multipart
  @POST("/user/new")
  Call<UserProfileBody> setUpInfo(@Part("fullname") RequestBody status, @Part("alias") RequestBody alias,
                                  @Part("gender") RequestBody gender,
                                  @Part("birthday") RequestBody birthday,
                                  @Part MultipartBody.Part avatar);

  @GET("/user/search")
  Call<List<UserBasicInfoBody>> searchForUser(@Query("query") String query);

  @GET("/user/search/recent")
  Call<List<UserBasicInfoBody>> loadRecentSearch(@Query("lastId") String lastId);

  @PUT("/user/search/{id}")
  Call<UserBasicInfoBody> onClickUser(@Path("id") String id);

  @PUT("/user/search/remove/{id}")
  Call<ResponseBody> deleteRecentSearch(@Path("id") String id);

}
