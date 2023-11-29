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

    static Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    static public void searchForUser(Context context, String query, final MutableLiveData<List<UserBasicInfo>> res) {

    }

    static public void onClickOnUser(Context context, final UserBasicInfo who, final MutableLiveData<String> listener) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).onSearchOnUser(who.getAlias());
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = response.code() == 200 ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.postValue("Failed");
            }
        });

    }

    static public void removeRecentProfileItem(Context context, final UserBasicInfo who, final MutableLiveData<String> listener) {
        Call<ResponseBody> req = retrofit.create(UserApi.class).removeProfileRecent(who.getAlias());
        req.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                String res = response.code() == 200 ? "Success" : "Failed";
                if (response.code() == 200) {
                } else {
                    System.out.println(response.code());
                }
                listener.postValue(res);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
                listener.postValue("Failed");
            }
        });

    }
}
