package com.example.socialmediaapp.application.session;


import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentDataSyncBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.SingleTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.post.CommentModel;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class CommentSessionHandler extends SessionHandler {

  protected Comment comment;
  private CommentDao dao;
  private MutableLiveData<CommentModel> commentData;
  private ReplyAccessHandler replyDataAccess;

  public CommentSessionHandler(Comment comment) {
    this.comment = comment;
    this.commentData = new MediatorLiveData<>();
    this.dao = DecadeDatabase.getInstance().getCommentDao();
  }

  public ReplyAccessHandler getReplyDataAccess() {
    return replyDataAccess;
  }

  public MutableLiveData<CommentModel> getCommentData() {
    return commentData;
  }

  @Override
  protected void init() {
    super.init();
    replyDataAccess = new ReplyAccessHandler(comment, new ReplyCommentAccessHelper(comment));
    commentData.postValue(ModelConvertor.convertToCommentModel(comment));
  }

  @Override
  protected void sync() {
    CommentDataSyncBody body = null;
    try {
      body = loadDataSync();
      int likeCount = body.getCountLike();

      comment.setLikeCount(likeCount);
      comment.setLiked(body.isLiked());

      dao.update(comment);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  @Override
  protected void clean() {
    dao.deleteComment(comment);
  }

  @Override
  protected void invalidate() {
    replyDataAccess.invalidate();
    super.invalidate();
  }


  public void doLike() {
    CommentModel model = commentData.getValue();
    boolean isLike = model.isLiked();
    if (isLike) return;
    model.setLiked(true);
    commentData.setValue(model);

    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      try {
        String status = likeComment();
        if (status.equals("Success")) {
          comment.setLiked(true);
          dao.update(comment);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    TaskRequest request = builder
            .setAlias("Comment" + comment.getId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }


  public void doUnLike() {
    CommentModel model = commentData.getValue();
    boolean isLike = model.isLiked();
    if (isLike) return;
    model.setLiked(false);
    commentData.setValue(model);

    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      try {
        String status = unlikeComment();
        if (status.equals("Success")) {
          comment.setLiked(false);
          dao.update(comment);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    TaskRequest request = builder
            .setAlias("Comment" + comment.getId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  private CommentDataSyncBody loadDataSync() throws IOException {
    Response<CommentDataSyncBody> res = HttpCallSupporter
            .create(CommentApi.class)
            .syncCommentData(comment.getId()).execute();
    return res.body();
  }

  private void doSynData() {
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
            .likeComment(comment.getId())
            .execute();
    return res.code() == 200 ? "Success" : "Failed";
  }

  private String unlikeComment() throws IOException {
    Response<ResponseBody> res = HttpCallSupporter.create(CommentApi.class)
            .unlikeComment(comment.getId())
            .execute();
    return res.code() == 200 ? "Success" : "Failed";
  }


  @Override
  protected void post(Runnable action) {
    super.post(() -> {
      action.run();
      doSynData();
    });
  }

  public static class SyncTask extends Task {
    private CommentSessionHandler handler;
    private CommentDao dao;

    public SyncTask() {
      super();
      dao = DecadeDatabase.getInstance().getCommentDao();
    }

    public void setHandler(CommentSessionHandler handler) {
      this.handler = handler;
    }

    @Override
    public void doTask() {
      try {
        CommentDataSyncBody body = handler.loadDataSync();

        int likeCount = body.getCountLike();

        handler.comment.setLikeCount(likeCount);
        dao.update(handler.comment);

        CommentModel commentModel = handler.commentData.getValue();
        commentModel.setCountLike(likeCount);
        handler.commentData.postValue(commentModel);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
