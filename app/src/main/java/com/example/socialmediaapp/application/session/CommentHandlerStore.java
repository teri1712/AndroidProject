package com.example.socialmediaapp.application.session;


import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.dao.RegistryDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.accesses.AccessRegistry;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.util.List;
import java.util.Map;

public class CommentHandlerStore extends HandlerStore<Comment> {
  private static CommentHandlerStore commentSessionStore;

  public static CommentHandlerStore getInstance() {
    if (commentSessionStore == null) {
      commentSessionStore = new CommentHandlerStore();
    }
    return commentSessionStore;
  }

  private UserBasicInfoDao userBasicInfoDao;
  private CommentDao dao;

  private CommentHandlerStore() {
    super();
  }

  @Override
  protected void init() {
    dao = db.getCommentDao();
    userBasicInfoDao = db.getUserBasicInfoDao();
  }

  @Override
  protected void cleanOrphanOnCreate() {
    dao.deleteAllOrphanByOrderedComment();
    RegistryDao registryDao = DecadeDatabase
            .getInstance()
            .getRegistryDao();
    List<AccessRegistry> rs = registryDao.findAllOrphanByAlias("Comment");
    for (AccessRegistry r : rs) {
      String commentId = r.getItemId();
      dao.deleteById(commentId);
    }
    registryDao.deleteListRegistry(rs);
  }

  @Override
  protected void createInLocal(Map<String, Object> itemPack) {
    UserBasicInfo userBasicInfo = (UserBasicInfo) itemPack.get("user basic info");
    Comment comment = (Comment) itemPack.get("comment");

    db.runInTransaction(() -> {
      int userId = (int) userBasicInfoDao.insert(userBasicInfo);

      comment.setUserInfoId(userId);
      dao.insert(comment);
    });
  }

  @Override
  protected Comment loadFromLocal(String itemId) {
    return dao.findCommentById(itemId);
  }

}
