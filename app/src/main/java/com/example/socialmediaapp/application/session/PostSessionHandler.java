package com.example.socialmediaapp.application.session;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.PostAccessSyncBody;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.post.PostDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.SingleTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.post.base.PostModel;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Response;

public class PostSessionHandler extends SessionHandler {
  private volatile Post post;
  private PostDao dao;
  private MutableLiveData<PostModel> postData;
  private CommentAccessHandler commentAccessHandler;

  protected PostSessionHandler(Post post) {
    super();
    this.post = post;
    this.postData = new MutableLiveData<>();
    this.dao = DecadeDatabase.getInstance().getPostDao();
  }

  @Override
  protected void init() {
    super.init();
    try {
      HttpCallSupporter
              .create(PostApi.class)
              .goOnline(post.getId())
              .execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    postData.postValue(ModelConvertor.convertToPostModel(post));
    commentAccessHandler = new CommentAccessHandler(post, new CommentAccessHelper(post.getId()));
  }

  @Override
  protected void sync() {
    try {
      PostAccessSyncBody body = loadDataSync();
      Integer likeCount = body.getLikeCount();
      Integer commentCount = body.getCommentCount();
      Integer shareCount = body.getShareCount();

      post.setLikeCount(likeCount);
      post.setCommentCount(commentCount);
      post.setShareCount(shareCount);
      post.setLiked(body.isLiked());

      dao.update(post);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void invalidate() {
    commentAccessHandler.invalidate();
    super.invalidate();
  }

  @Override
  protected void clean() {
    dao.deletePost(post);
  }

  public CommentAccessHandler getCommentAccessHandler() {
    return commentAccessHandler;
  }

  public LiveData<PostModel> getPostData() {
    return postData;
  }

  public void doLike() {
    PostModel model = postData.getValue();
    boolean isLike = model.isLiked();
    if (isLike) return;
    model.setLiked(true);
    postData.setValue(model);

    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      try {
        String status = likePost();
        if (status.equals("Success")) {
          post.setLiked(true);
          dao.update(post);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    TaskRequest request = builder
            .setAlias("Post"+post.getId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  public void doUnLike() {
    PostModel model = postData.getValue();
    boolean isLike = model.isLiked();
    if (!isLike) return;
    model.setLiked(false);
    postData.setValue(model);

    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(() -> {
      try {
        String status = unLikePost();
        if (status.equals("Success")) {
          post.setLiked(false);
          dao.update(post);
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    });
    TaskRequest request = builder
            .setAlias("Post" + post.getId())
            .setWillRestore(false)
            .build();
    postTask(request);
  }

  private String likePost() throws IOException {
    Response<ResponseBody> res = HttpCallSupporter.create(PostApi.class)
            .likePost(post.getId())
            .execute();
    return res.code() == 200 ? "Success" : "Failed";
  }

  private String unLikePost() throws IOException {
    Response<ResponseBody> res = HttpCallSupporter.create(PostApi.class)
            .unlikePost(post.getId())
            .execute();
    return res.code() == 200 ? "Success" : "Failed";
  }

  private PostAccessSyncBody loadDataSync() throws IOException {
    Response<PostAccessSyncBody> res = HttpCallSupporter.create(PostApi.class)
            .syncPostData(post.getId())
            .execute();
    PostAccessSyncBody body = res.body();
    return body;
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

  @Override
  protected void post(Runnable action) {
    super.post(() -> {
      action.run();
      doSyncData();
    });
  }

  public static class SyncTask extends Task {
    private PostSessionHandler handler;
    private PostDao dao;

    public SyncTask() {
      super();
      dao = DecadeDatabase.getInstance().getPostDao();
    }

    public void setHandler(PostSessionHandler handler) {
      this.handler = handler;
    }

    @Override
    public void doTask() {
      PostAccessSyncBody body = null;
      try {
        body = handler.loadDataSync();

        Integer likeCount = body.getLikeCount();
        Integer commentCount = body.getCommentCount();
        Integer shareCount = body.getShareCount();

        handler.post.setLikeCount(likeCount);
        handler.post.setCommentCount(commentCount);
        handler.post.setShareCount(shareCount);

        dao.update(handler.post);

        PostModel postModel = handler.postData.getValue();
        postModel.setLikeCount(likeCount);
        postModel.setCommentCount(commentCount);
        postModel.setShareCount(shareCount);

        handler.postData.postValue(postModel);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
