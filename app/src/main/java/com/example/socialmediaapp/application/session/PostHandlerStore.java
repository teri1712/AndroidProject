package com.example.socialmediaapp.application.session;

import android.util.ArrayMap;

import com.example.socialmediaapp.api.PostApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.post.PostDao;
import com.example.socialmediaapp.application.dao.RegistryDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.accesses.AccessRegistry;
import com.example.socialmediaapp.application.entity.post.ImagePost;
import com.example.socialmediaapp.application.entity.post.MediaPost;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PostHandlerStore extends HandlerStore<Post> {
  private static PostHandlerStore postSessionStore;

  public static PostHandlerStore getInstance() {
    if (postSessionStore == null) {
      postSessionStore = new PostHandlerStore();
    }
    return postSessionStore;
  }

  private UserBasicInfoDao userBasicInfoDao;
  private PostDao postDao;

  @Override
  protected void init() {
    super.init();
    userBasicInfoDao = db.getUserBasicInfoDao();
    postDao = db.getPostDao();
  }

  @Override
  protected void cleanOrphanOnCreate() {
    postDao.deleteAllAccessOrphan();
    RegistryDao registryDao = DecadeDatabase.getInstance()
            .getRegistryDao();
    List<AccessRegistry> rs = registryDao.findAllOrphanByAlias("Post");
    for (AccessRegistry r : rs) {
      String postId = r.getItemId();
      postDao.deleteById(postId);
    }
    registryDao.deleteListRegistry(rs);
  }

  @Override
  protected void createInLocal(Map<String, Object> itemPack) {
    Post post = (Post) itemPack.get("post");
    ImagePost imagePost = (ImagePost) itemPack.get("image post");
    MediaPost mediaPost = (MediaPost) itemPack.get("media post");
    UserBasicInfo userBasicInfo = (UserBasicInfo) itemPack.get("user basic info");
    db.runInTransaction(() -> {
      int userId = (int) userBasicInfoDao.insert(userBasicInfo);
      post.setUserInfoId(userId);

      postDao.insert(post);
      if (imagePost != null) {
        imagePost.setPostId(post.getId());
        postDao.insertImagePost(imagePost);
      }
      if (mediaPost != null) {
        mediaPost.setPostId(post.getId());
        postDao.insertMediaPost(mediaPost);
      }
    });
  }

  @Override
  protected Post loadFromLocal(String postId) {
    return postDao.findPostById(postId);
  }

}
