package com.example.socialmediaapp.container.session;


import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.PostDataSyncBody;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.session.helper.DataAccessHelper;
import com.example.socialmediaapp.container.session.helper.PostAccessHelper;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.repo.interceptor.FetchResponse;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.subjects.PublishSubject;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostSessionHandler extends SessionHandler {
    public class PostDataSync {
        public Integer likeCount, commentCount, shareCount;
    }

    private Integer postId;
    private MutableLiveData<PostDataSync> dataSyncEmitter;
    private WorkManager workManager = ApplicationContainer.getInstance().workManager;

    public PostSessionHandler(Integer postId) {
        super();
        this.postId = postId;
        dataSyncEmitter = new MutableLiveData<>();
    }

    public MutableLiveData<PostDataSync> getDataSyncEmitter() {
        return dataSyncEmitter;
    }

    public MutableLiveData<String> doLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                Data query = new Data.Builder().putString("action", "like").putInt("post id", postId).build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(LikeHandleWorker.class).setInputData(query).build();
                workManager.enqueue(req);

                ListenableFuture<WorkInfo> future = workManager.getWorkInfoById(req.getId());
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            callBack.postValue("Success");
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.postValue("Failed");
                        }
                    }
                });
            }
        });
        return callBack;
    }

    public MutableLiveData<String> doUnLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(new Runnable() {
            @Override
            public void run() {
                Data query = new Data.Builder().putString("action", "unlike").putInt("post id", postId).build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(LikeHandleWorker.class).setInputData(query).build();
                workManager.enqueue(req);

                ListenableFuture<WorkInfo> future = workManager.getWorkInfoById(req.getId());
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            callBack.postValue("Success");
                        } catch (Exception e) {
                            e.printStackTrace();
                            callBack.postValue("Failed");
                        }
                    }
                });
            }
        });
        return callBack;
    }

    public void requestSyncData(Integer postId) {
        post(new Runnable() {
            @Override
            public void run() {
                Data query = new Data.Builder().putInt("post id",postId).build();
                OneTimeWorkRequest req = new OneTimeWorkRequest.Builder(LikeHandleWorker.class).setInputData(query).build();
                workManager.enqueue(req);

                ListenableFuture<WorkInfo> future = workManager.getWorkInfoById(req.getId());
                addFutureCallBack(future, new Runnable() {
                    @Override
                    public void run() {
                        WorkInfo workInfo = null;
                        try {
                            workInfo = future.get();
                            Data out = workInfo.getOutputData();
                            Gson gson = new Gson();
                            PostDataSync postDataSync = gson.fromJson(out.getString("result"), PostDataSync.class);
                            dataSyncEmitter.postValue(postDataSync);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void clean() {
        super.clean();
    }

    public class LikeHandleWorker extends Worker {
        private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;

        public LikeHandleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        private String likePost(Integer postId) throws IOException {
            Call<ResponseBody> req = retrofit.create(PostApi.class).likePost(postId);
            Response<ResponseBody> res = req.execute();
            return res.code() == 200 ? "Success" : "Failed";
        }

        private String unLikePost(Integer postId) throws IOException {
            Call<ResponseBody> req = retrofit.create(PostApi.class).unlikePost(postId);
            Response<ResponseBody> res = req.execute();
            return res.code() == 200 ? "Success" : "Failed";
        }

        @NonNull
        @Override
        public Result doWork() {
            Data query = getInputData();
            String action = query.getString("action");
            Integer postId = query.getInt("post id", -1);

            try {
                String res = action.equals("like") ? likePost(postId) : unLikePost(postId);
                return Result.success(new Data.Builder().putString("result", res).build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Result.failure();
        }
    }

    public class DataSyncWorker extends Worker {
        private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;


        public DataSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Data query = getInputData();
            Integer postId = query.getInt("post id", -1);
            Call<PostDataSyncBody> req = retrofit.create(PostApi.class).syncPostData(postId);
            try {
                Response<PostDataSyncBody> res = req.execute();

                PostDataSyncBody body = res.body();

                PostDataSync postDataSync = new PostDataSync();
                postDataSync.commentCount = body.commentCount;
                postDataSync.likeCount = body.likeCount;
                postDataSync.shareCount = body.shareCount;

                String json = new Gson().toJson(postDataSync);

                return Result.success(new Data.Builder().putString("result", json).build());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return Result.failure();
        }
    }
}
