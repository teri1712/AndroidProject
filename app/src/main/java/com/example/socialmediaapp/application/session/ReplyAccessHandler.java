package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.ReplyCommentBody;
import com.example.socialmediaapp.api.entities.requests.CommentAccessSyncBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.network.SingleTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.network.TaskDetails;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ReplyAccessHandler
        extends LongPollingHandler<HandlerAccess, ReplyCommentBody> {
  private Comment comment;
  private CommentDao dao;
  private MutableLiveData<Integer> totalComment;

  public ReplyAccessHandler(
          Comment comment,
          ReplyCommentAccessHelper accessHelper) {
    super(accessHelper, accessHelper);
    this.comment = comment;
    this.dao = DecadeDatabase.getInstance().getCommentDao();
    totalComment = new MutableLiveData<>(comment.getCountReply());
  }

  public LiveData<Integer> getTotalComment() {
    return totalComment;
  }

  @Override
  protected Call<List<ReplyCommentBody>> doPoll() {
    return null;
  }

  public void updateCountCommentToUi(Integer cnt) {
    totalComment.postValue(cnt);
  }

  @Override
  protected void onUpdateCompleted(Map<String, Object> data) {
    super.onUpdateCompleted(data);
    List<ReplyCommentBody> bodies = (List<ReplyCommentBody>) data.get("items");
    comment.setCountReply(comment.getCountReply() + bodies.size());
    dao.update(comment);
  }

  @Override
  protected void post(Runnable action) {
    Runnable wrap = () -> {
      if (!longPollingOpened) {
        Bundle data = new Bundle();
        data.putString("comment id", comment.getId());
        SingleTaskRequest.Builder builder = new SingleTaskRequest.Builder();
        SyncTask task = new SingleTaskRequest.Builder().fromTask(SyncTask.class);
        task.setHandler(ReplyAccessHandler.this);
        SingleTaskRequest request = builder
                .setWillRestore(false)
                .setData(data)
                .build();
        postTask(request);
      }
      action.run();
    };
    super.post(wrap);
  }

  public static class SyncTask extends Task {
    private volatile ReplyAccessHandler handler;
    private CommentDao dao;

    public SyncTask() {
      super();
      dao = DecadeDatabase.getInstance().getCommentDao();
    }

    public void setHandler(ReplyAccessHandler handler) {
      this.handler = handler;
    }

    @Override
    public void doTask() {
      Bundle data = getData();
      String commentId = data.getString("comment id");
      try {
        CommentAccessSyncBody body = HttpCallSupporter
                .create(CommentApi.class)
                .syncCommentAccess(commentId)
                .execute()
                .body();
        final int countReply = body.getCountReply();
        handler.post(() -> {
          if (handler.longPollingOpened)
            return;

          Comment comment = handler.comment;
          comment.setCountReply(countReply);
          dao.update(comment);
          handler.updateCountCommentToUi(countReply);
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
