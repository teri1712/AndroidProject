package com.example.socialmediaapp.application.session;

import android.os.Bundle;


import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.api.entities.PostAccessSyncBody;
import com.example.socialmediaapp.application.dao.post.PostDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.network.SingleTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.repo.core.utilities.DataEmit;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class CommentAccessHandler
        extends LongPollingHandler<HandlerAccess, CommentBody> {
  private volatile Post post;
  private PostDao dao;

  public CommentAccessHandler(Post post, CommentAccessHelper accessHelper) {
    super(accessHelper, accessHelper);
    dao = DecadeDatabase.getInstance().getPostDao();
    this.post = post;
  }


  @Override
  protected void onUpdateCompleted(Map<String, Object> data) {
    super.onUpdateCompleted(data);
    List<CommentBody> bodies = (List<CommentBody>) data.get("items");
    post.setCommentCount(post.getCommentCount() + bodies.size());
    dao.update(post);
  }


  @Override
  protected void post(Runnable action) {
    Runnable wrap = () -> {
      if (!longPollingOpened) {
        Bundle data = new Bundle();
        data.putString("post id", post.getId());
        SingleTaskRequest.Builder builder = new SingleTaskRequest.Builder();
        SyncTask task = new SingleTaskRequest.Builder().fromTask(SyncTask.class);
        task.setHandler(CommentAccessHandler.this);
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

  @Override
  protected Call<List<CommentBody>> doPoll() {
    return null;
  }

  public static class SyncTask extends Task {
    private volatile CommentAccessHandler handler;
    private PostDao dao;

    public SyncTask() {
      super();
      dao = DecadeDatabase.getInstance().getPostDao();
    }

    public void setHandler(CommentAccessHandler handler) {
      this.handler = handler;
    }

    @Override
    public void doTask() {
      Bundle data = getData();
      String postId = data.getString("post id");
      try {
        PostAccessSyncBody body = HttpCallSupporter
                .create(PostApi.class)
                .syncPostData(postId)
                .execute()
                .body();
        final int countComment = body.getCommentCount();
        handler.post(() -> {
          if (handler.longPollingOpened) return;

          Post post = handler.post;
          post.setCommentCount(countComment);
          dao.update(post);
          handler.uiEmitter.onNext(new DataEmit(null, "Success", "hint"));
        });
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
