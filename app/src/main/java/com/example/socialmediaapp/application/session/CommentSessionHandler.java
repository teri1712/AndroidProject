package com.example.socialmediaapp.application.session;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.CommentDataSyncBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentSessionHandler extends SessionHandler {
    private Integer commentId;
    private MutableLiveData<Comment> dataSyncEmitter;
    private WorkManager workManager = ApplicationContainer.getInstance().workManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private String userAlias;

    private Executor worker;

    public CommentSessionHandler(Comment comment) {
        super();
        this.commentId = comment.getId();
        this.userAlias = comment.getAuthor().getAlias();
        dataSyncEmitter = new MutableLiveData<>(comment);
    }

    public MutableLiveData<Comment> getDataSyncEmitter() {
        return dataSyncEmitter;
    }

    public MutableLiveData<String> doLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            try {
                callBack.postValue(likeComment(commentId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");
        }));
        return callBack;
    }


    public MutableLiveData<String> doUnLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            try {
                callBack.postValue(unlikeComment(commentId));
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");
        }));
        return callBack;
    }

    public void requestSyncData(Integer postId) {
        post(() -> postToWorker(() -> {
            Call<CommentDataSyncBody> req = retrofit.create(PostApi.class).syncCommenData(commentId);
            try {
                Response<CommentDataSyncBody> res = req.execute();

                CommentDataSyncBody body = res.body();
                Comment comment = dataSyncEmitter.getValue();
                comment.setCountLike(body.likeCount);
                comment.setCountComment(body.commentCount);
                dataSyncEmitter.setValue(comment);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
    }

    private String likeComment(Integer commentId) throws IOException {
        Call<ResponseBody> req = retrofit.create(PostApi.class).likeComment(commentId);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : "Failed";
    }

    private String unlikeComment(Integer commentId) throws IOException {
        Call<ResponseBody> req = retrofit.create(PostApi.class).unlikeComment(commentId);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : "Failed";
    }

    private void postToWorker(Runnable runnable) {
        worker.execute(runnable);
    }
}
