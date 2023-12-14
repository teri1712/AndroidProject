package com.example.socialmediaapp.services;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.AuthenApi;
import com.example.socialmediaapp.apis.MediaApi;
import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ServiceApi {

}
