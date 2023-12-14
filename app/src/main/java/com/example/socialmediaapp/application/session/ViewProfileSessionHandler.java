package com.example.socialmediaapp.application.session;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.session.helper.UserPostAccessHelper;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ViewProfileSessionHandler extends SessionHandler {
    protected MutableLiveData<UserProfile> dataSyncEmitter;
    protected DataAccessHandler<Post> postRepositorySession;
    protected LiveData<SessionHandler> avatarPostSession;
    protected LiveData<SessionHandler> backgroundPostSession;
    protected String userAlias;
    protected Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    protected DtoConverter dtoConverter;
    protected SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;

    public ViewProfileSessionHandler(String userAlias) {
        super();
        this.userAlias = userAlias;
        dataSyncEmitter = new MutableLiveData<>();
        dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
        LiveData<Integer> avatarPostSessionId = Transformations.switchMap(dataSyncEmitter, new Function<UserProfile, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(UserProfile input) {
                ImagePost avatarPost = input.getAvatarPost();
                return avatarPost == null ? null : sessionRegistry.bindSession(new PostSessionHandler(avatarPost));
            }
        });
        avatarPostSession = Transformations.switchMap(avatarPostSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return sessionRepository.getSessionById(input);
            }
        });
        LiveData<Integer> backgroundPostSessionId = Transformations.switchMap(dataSyncEmitter, new Function<UserProfile, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(UserProfile input) {
                ImagePost backgroundPost = input.getBackgroundPost();
                return backgroundPost == null ? null : sessionRegistry.bindSession(new PostSessionHandler(backgroundPost));
            }
        });
        backgroundPostSession = Transformations.switchMap(backgroundPostSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return sessionRepository.getSessionById(input);
            }
        });
    }

    public LiveData<SessionHandler> getAvatarPostSession() {
        return avatarPostSession;
    }

    public LiveData<SessionHandler> getBackgroundPostSession() {
        return backgroundPostSession;
    }

    public MutableLiveData<UserProfile> getDataSyncEmitter() {
        return dataSyncEmitter;
    }

    public DataAccessHandler<Post> getPostRepositorySession() {
        return postRepositorySession;
    }


    @Override
    protected void init() {
        super.init();
        postRepositorySession = new DataAccessHandler<>(new UserPostAccessHelper(userAlias));
        sessionRegistry.bind(postRepositorySession);

        loadUserProfile();
    }

    private void loadUserProfile() {
        postToWorker(() -> {
            Call<UserProfileBody> req = retrofit.create(UserApi.class).loadUserProfile(userAlias);
            try {
                Response<UserProfileBody> res = req.execute();
                dataSyncEmitter.postValue(dtoConverter.convertToUserProfile(res.body()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public MutableLiveData<String> sendFriendRequest() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            callBack.postValue("Success");
            Call<ResponseBody> req = retrofit.create(UserApi.class).sendFriendRequest(userAlias);
            Response<ResponseBody> res = null;
            try {
                res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");

        }));
        return callBack;
    }

    public MutableLiveData<String> acceptFriendRequest() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            Call<ResponseBody> req = retrofit.create(UserApi.class).acceptFriendRequest(userAlias);
            Response<ResponseBody> res = null;
            try {
                res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");
        }));
        return callBack;
    }

    public MutableLiveData<String> rejectFriendRequest() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            Call<ResponseBody> req = retrofit.create(UserApi.class).rejectFriendRequest(userAlias);
            Response<ResponseBody> res = null;
            try {
                res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");

        }));
        return callBack;
    }

    public MutableLiveData<String> cancelFriendRequest() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            Call<ResponseBody> req = retrofit.create(UserApi.class).cancelFriendRequest(userAlias);
            Response<ResponseBody> res = null;
            try {
                res = req.execute();
                if (res.code() == 200) {
                    callBack.postValue("Success");
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");

        }));
        return callBack;
    }
}
