package com.example.socialmediaapp.apis;

import com.example.socialmediaapp.apis.entities.HomeEntranceBody;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.apis.entities.UserSessionBody;
import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;

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

    @GET("/user/profile/{alias}")
    Call<UserProfileBody> loadUserProfile(@Path("alias") String alias);
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
    Call<UserSessionBody> setUpInfo(@Part("fullname") RequestBody status, @Part("alias") RequestBody type,
                                 @Part("gender") RequestBody gender,
                                 @Part("birthday") RequestBody birthday,
                                 @Part MultipartBody.Part avatar);
    @GET("/user/search")
    Call<List<UserBasicInfoBody>> searchForUser(@Query("query") String query);
    @GET("/user/search/recent")
    Call<List<UserBasicInfoBody>> fetchRecentSearch();
    @PUT("/user/search/{alias}")
    Call<UserBasicInfoBody> addToRecentSearch(@Path("alias") String alias);
    @PUT("/user/search/remove/{alias}")
    Call<ResponseBody> deleteRecentSearch(@Path("alias") String alias);

    @PUT("/user/friend/add/{alias}")
    Call<ResponseBody> sendFriendRequest(@Path("alias") String alias);

    @PUT("/user/friend/cancel/{alias}")
    Call<ResponseBody> cancelFriendRequest(@Path("alias") String alias);

    @PUT("/user/friend/accept/{alias}")
    Call<ResponseBody> acceptFriendRequest(@Path("alias") String alias);
    @PUT("/user/friend/reject/{alias}")
    Call<ResponseBody> rejectFriendRequest(@Path("alias") String alias);

}
