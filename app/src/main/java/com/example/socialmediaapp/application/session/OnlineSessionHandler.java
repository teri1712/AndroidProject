package com.example.socialmediaapp.application.session;


import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.AuthenApi;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.services.ServiceApi;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Multipart;

public class OnlineSessionHandler extends SessionHandler {

    private SessionRepository sessionRepository;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;

    public OnlineSessionHandler() {
        super();
        sessionRepository = new SessionRepository();
    }

    public SessionRepository getSessionRepository() {
        return sessionRepository;
    }

    public void networkInterrupt(Throwable t) {
        post(new Runnable() {
            @Override
            public void run() {
                handleNetworkIssue(t);
                interrupt();
            }
        });
    }

    public void networkBack() {
        post(new Runnable() {
            @Override
            public void run() {
                onlineSessionStrategy();
                resume();
            }
        });
    }

    private void handleNetworkIssue(Throwable t) {
    }

    private void onlineSessionStrategy() {
    }

    public MutableLiveData<String> authenticate(String username,String password) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> {
            AuthenApi request = retrofit.create(AuthenApi.class);
            Call<ResponseBody> req = request.login(username, password);
            try {
                Response<ResponseBody> res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                } else {
                    callBack.postValue(res.errorBody().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                callBack.postValue("network issue");
            }
        });
        return callBack;
    }
    public MutableLiveData<String> signUp(String username,String password) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> {
            AuthenApi request = retrofit.create(AuthenApi.class);
            Call<ResponseBody> req = request.signup(username, password);
            try {
                Response<ResponseBody> res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                } else {
                    callBack.postValue(res.errorBody().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                callBack.postValue("network issue");
            }
        });
        return callBack;
    }
    public MutableLiveData<String> logout() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> {
            AuthenApi request = retrofit.create(AuthenApi.class);
            Call<ResponseBody> req = request.logout();
            try {
                Response<ResponseBody> res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                } else {
                    callBack.postValue(res.errorBody().string());
                }
            } catch (IOException e) {
                e.printStackTrace();
                callBack.postValue("network issue");
            }
        });
        return callBack;
    }
}
