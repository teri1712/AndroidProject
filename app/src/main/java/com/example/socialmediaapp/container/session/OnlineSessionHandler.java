package com.example.socialmediaapp.container.session;


import android.content.ContentResolver;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.se.omapi.Session;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.converter.HttpBodyConverter;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnlineSessionHandler extends SessionHandler {

    private HashMap<Integer, SessionHandler> sessionHandlerHashMap;

    public OnlineSessionHandler() {
        super();
        sessionHandlerHashMap = new HashMap<>();
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

    public Integer createSession(SessionHandler sessionHandler) {
        Integer id = sessionHandlerHashMap.size();
        sessionHandlerHashMap.put(id, sessionHandler);
        return id;
    }

    public SessionHandler removeSessionId(Integer id) {
        return sessionHandlerHashMap.remove(id);
    }

    public MutableLiveData<SessionHandler> getSessionById(Integer sessionId) {
        assert sessionId != null;
        MutableLiveData<SessionHandler> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                callBack.postValue(sessionHandlerHashMap.get(sessionId));
            }
        });
        return callBack;
    }

}
