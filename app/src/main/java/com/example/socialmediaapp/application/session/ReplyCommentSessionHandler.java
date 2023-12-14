package com.example.socialmediaapp.application.session;


import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.CommentApi;
import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.CommentDataSyncBody;
import com.example.socialmediaapp.apis.entities.ReplyCommentDataSyncBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.helper.ReplyCommentAccessHelper;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReplyCommentSessionHandler extends SessionHandler {
    private Integer commentId;
    private MutableLiveData<Boolean> likeSync;
    private MutableLiveData<ReplyComment> dataSyncEmitter;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;

    public MutableLiveData<Boolean> getLikeSync() {
        return likeSync;
    }

    public ReplyCommentSessionHandler(ReplyComment comment) {
        super();
        this.commentId = comment.getId();
        dataSyncEmitter = new MutableLiveData<>(comment);
        likeSync = new MutableLiveData<>(comment.isLiked());
    }
    public MutableLiveData<ReplyComment> getDataSyncEmitter() {
        return dataSyncEmitter;
    }

    public MutableLiveData<String> doLike() {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            try {
                String status = likeComment();
                callBack.postValue(status);
                return;
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
                String status = unlikeComment();
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
            Call<ReplyCommentDataSyncBody> req = retrofit.create(CommentApi.class).syncReplyCommentData(commentId);
            try {
                Response<ReplyCommentDataSyncBody> res = req.execute();

                ReplyCommentDataSyncBody body = res.body();
                ReplyComment comment = dataSyncEmitter.getValue();
                comment.setCountLike(body.getCountLike());
                dataSyncEmitter.postValue(comment);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    private String likeComment() throws IOException {
        Call<ResponseBody> req = retrofit.create(CommentApi.class).likeReplyComment(commentId);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : "Failed";
    }

    private String unlikeComment() throws IOException {
        Call<ResponseBody> req = retrofit.create(CommentApi.class).unlikeReplyComment(commentId);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : "Failed";
    }


    @Override
    protected void post(Runnable action) {
        super.post(() -> {
            action.run();
            requestSyncData();
        });
    }
}
