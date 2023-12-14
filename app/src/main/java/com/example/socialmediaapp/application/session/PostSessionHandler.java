package com.example.socialmediaapp.application.session;


import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.work.Data;
import androidx.work.ListenableWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.PostDataSyncBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.helper.CommentAccessHelper;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodel.PostDataViewModel;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class PostSessionHandler extends SessionHandler {
    private Integer postId;
    private MutableLiveData<Post> dataSyncEmitter;
    private Integer commentSessionId;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private MutableLiveData<Boolean> likeSync;

    public PostSessionHandler(Post post) {
        super();
        this.postId = post.getId();
        dataSyncEmitter = new MutableLiveData<>(post);
        likeSync = new MutableLiveData<>(post.isLiked());
    }

    public Integer getCommentSessionId() {
        return commentSessionId;
    }

    public MutableLiveData<Post> getDataSyncEmitter() {
        return dataSyncEmitter;
    }

    @Override
    protected void init() {
        super.init();
        DataAccessHandler<Comment> commentDataAccessHandler = new DataAccessHandler<>(new CommentAccessHelper(postId));
        commentSessionId = sessionRegistry.bind(commentDataAccessHandler);
    }

    public MutableLiveData<String> doLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            try {
                String status = likePost();
                callBack.postValue(status);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");

        }));
        return callBack;
    }

    public MutableLiveData<Boolean> getLikeSync() {
        return likeSync;
    }

    public MutableLiveData<String> doUnLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            try {
                String status = unLikePost();
                callBack.postValue(status);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
            callBack.postValue("Failed");

        }));
        return callBack;
    }

    private void requestSyncData() {
        postToWorker(() -> {
            Call<PostDataSyncBody> req = retrofit.create(PostApi.class).syncPostData(postId);
            try {
                Response<PostDataSyncBody> res = req.execute();
                PostDataSyncBody body = res.body();

                Post post = dataSyncEmitter.getValue();
                post.setLikeCount(body.getLikeCount());
                post.setCommentCount(body.getCommentCount());
                post.setShareCount(body.getShareCount());
                dataSyncEmitter.postValue(post);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void post(Runnable action) {
        super.post(() -> {
            action.run();
            requestSyncData();
        });
    }

    private String likePost() throws IOException {
        Call<ResponseBody> req = retrofit.create(PostApi.class).likePost(postId);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : "Failed";
    }

    private String unLikePost() throws IOException {
        Call<ResponseBody> req = retrofit.create(PostApi.class).unlikePost(postId);
        Response<ResponseBody> res = req.execute();

        return res.code() == 200 ? "Success" : "Failed";
    }

}
