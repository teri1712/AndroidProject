package com.example.socialmediaapp.application.session;


import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.apis.CommentApi;
import com.example.socialmediaapp.apis.PostApi;
import com.example.socialmediaapp.apis.entities.CommentDataSyncBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.helper.CommentAccessHelper;
import com.example.socialmediaapp.application.session.helper.ReplyCommentAccessHelper;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CommentSessionHandler extends SessionHandler {
    private Integer commentId;
    private MutableLiveData<Comment> dataSyncEmitter;
    private MutableLiveData<Boolean> likeSync;
    private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
    private Integer replyCommentSessionId;


    public CommentSessionHandler(Comment comment) {
        super();
        this.commentId = comment.getId();
        dataSyncEmitter = new MutableLiveData<>(comment);
        likeSync = new MutableLiveData<>(comment.isLiked());
    }

    public MutableLiveData<Boolean> getLikeSync() {
        return likeSync;
    }

    @Override
    protected void init() {
        super.init();

        DataAccessHandler<ReplyComment> commentDataAccessHandler = new DataAccessHandler<>(new ReplyCommentAccessHelper(commentId));
        replyCommentSessionId = sessionRegistry.bind(commentDataAccessHandler);
    }

    public Integer getReplyCommentSessionId() {
        return replyCommentSessionId;
    }

    public MutableLiveData<Comment> getDataSyncEmitter() {
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
            Call<CommentDataSyncBody> req = retrofit.create(CommentApi.class).syncCommentData(commentId);
            try {
                Response<CommentDataSyncBody> res = req.execute();

                CommentDataSyncBody body = res.body();
                Comment comment = dataSyncEmitter.getValue();
                comment.setCountLike(body.getCountLike());
                comment.setCountComment(body.getCountComment());
                dataSyncEmitter.postValue(comment);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String likeComment() throws IOException {
        Call<ResponseBody> req = retrofit.create(CommentApi.class).likeComment(commentId);
        Response<ResponseBody> res = req.execute();
        return res.code() == 200 ? "Success" : "Failed";
    }

    private String unlikeComment() throws IOException {
        Call<ResponseBody> req = retrofit.create(CommentApi.class).unlikeComment(commentId);
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
