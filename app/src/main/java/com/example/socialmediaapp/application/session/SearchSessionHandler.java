package com.example.socialmediaapp.application.session;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchSessionHandler extends SessionHandler {
    private RecentSearchAccessHandler recentSearchSession;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private DtoConverter dtoConverter;
    private Thread curFetchingThread;

    public SearchSessionHandler() {
        super();
        dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
    }

    @Override
    protected void init() {
        super.init();
        recentSearchSession = new RecentSearchAccessHandler();
        sessionRegistry.bind(recentSearchSession);
    }

    public RecentSearchAccessHandler getRecentSearchSession() {
        return recentSearchSession;
    }

    public MutableLiveData<List<UserBasicInfo>> searchForUsers(String query) {
        MutableLiveData<List<UserBasicInfo>> callBack = new MutableLiveData<>();
        post(() -> {
            if (curFetchingThread != null) {
                curFetchingThread.interrupt();
            }
            curFetchingThread = new FetchThread(query, callBack);
            curFetchingThread.start();
        });
        return callBack;
    }

    private class FetchThread extends Thread {
        private String query;
        private MutableLiveData<List<UserBasicInfo>> callBack;

        @Override
        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                return;
            }

            Call<List<UserBasicInfoBody>> req = retrofit.create(UserApi.class).searchForUser(query);
            Response<List<UserBasicInfoBody>> res = null;
            try {
                res = req.execute();
                List<UserBasicInfoBody> users = res.body();
                List<UserBasicInfo> batch = new ArrayList<>();
                for (UserBasicInfoBody u : users) {
                    batch.add(dtoConverter.convertToModelUserBasicInfo(u));
                }
                callBack.postValue(batch);
            } catch (IOException e) {
                e.printStackTrace();
                callBack.postValue(new ArrayList<>());
            }
        }

        public FetchThread(String query, MutableLiveData<List<UserBasicInfo>> callBack) {
            this.query = query;
            this.callBack = callBack;
        }
    }
}
