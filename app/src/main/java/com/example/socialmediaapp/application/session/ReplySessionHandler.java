package com.example.socialmediaapp.application.session;


import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.ReplyCommentDataSyncBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.SingleTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.post.ReplyModel;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ReplySessionHandler extends SessionHandler {
  protected ReplyComment replyComment;
  private MutableLiveData<ReplyModel> replyData;
  private CommentDao dao;

  public ReplySessionHandler(ReplyComment replyComment) {
    super();
    this.replyComment = replyComment;
    this.replyData = new MutableLiveData<>();
    this.dao = DecadeDatabase.getInstance().getCommentDao();

  }

  public MutableLiveData<ReplyModel> getReplyData() {
    return replyData;
  }

  @Override
  protected void init() {
    super.init();
    replyData.postValue(ModelConvertor.convertToReplyModel(replyComment));
  }

  @Override
  protected void sync() {
    ReplyCommentDataSyncBody body = null;
    try {
      body = loadDataSync();
      replyComment.setLikeCount(body.getCountLike());
      replyComment.setLiked(body.isLike());

      dao.updateReplyComment(replyComment);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void clean() {
    dao.deleteReplyComment(replyComment);
  }

  public void doLike() {
    ReplyModel model = replyData.getValue();
    boolean isLike = model.isLiked();
    if (isLike) return;
    model.setLiked(true);
    replyData.setValue(model);

    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      try {
        String status = likeComment();
        if (status.equals("Success")) {
          replyComment.setLiked(true);
          dao.updateReplyComment(replyComment);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    TaskRequest request = builder
            .setAlias("Reply" + replyComment.getId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  public void doUnLike() {
    ReplyModel model = replyData.getValue();
    boolean isLike = model.isLiked();
    if (isLike) return;
    model.setLiked(false);
    replyData.setValue(model);


    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      try {
        String status = unlikeComment();
        if (status.equals("Success")) {
          replyComment.setLiked(false);
          dao.updateReplyComment(replyComment);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    TaskRequest request = builder
            .setAlias("Reply" + replyComment.getId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  private ReplyCommentDataSyncBody loadDataSync() throws IOException {
    Response<ReplyCommentDataSyncBody> res = HttpCallSupporter.create(CommentApi.class)
            .syncReplyCommentData(replyComment.getId())
            .execute();
    return res.body();
  }

  private void doSyncData() {
    SingleTaskRequest.Builder builder = new SingleTaskRequest.Builder();
    SyncTask task = new SingleTaskRequest.Builder().fromTask(SyncTask.class);
    task.setHandler(this);
    SingleTaskRequest request = builder
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  private String likeComment() throws IOException {
    Response<ResponseBody> res = HttpCallSupporter.create(CommentApi.class)
            .likeReplyComment(replyComment.getId())
            .execute();
    return res.code() == 200 ? "Success" : "Failed";
  }

  private String unlikeComment() throws IOException {
    Response<ResponseBody> res = HttpCallSupporter.create(CommentApi.class)
            .unlikeReplyComment(replyComment.getId())
            .execute();
    return res.code() == 200 ? "Success" : "Failed";
  }


  @Override
  protected void post(Runnable action) {
    super.post(() -> {
      action.run();
      doSyncData();
    });
  }

  public static class SyncTask extends Task {
    private ReplySessionHandler handler;
    private CommentDao dao;

    public SyncTask() {
      super();
      dao = DecadeDatabase.getInstance().getCommentDao();
    }

    public void setHandler(ReplySessionHandler handler) {
      this.handler = handler;
    }

    @Override
    public void doTask() {
      ReplyCommentDataSyncBody body = null;
      try {
        body = handler.loadDataSync();
        handler.replyComment.setLikeCount(body.getCountLike());
        dao.updateReplyComment(handler.replyComment);

        ReplyModel commentModel = handler.replyData.getValue();
        commentModel.setCountLike(body.getCountLike());
        handler.replyData.postValue(commentModel);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
