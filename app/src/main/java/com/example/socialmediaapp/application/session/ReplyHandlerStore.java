package com.example.socialmediaapp.application.session;


import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.comment.CommentDao;
import com.example.socialmediaapp.application.dao.RegistryDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.accesses.AccessRegistry;
import com.example.socialmediaapp.application.entity.reply.ReplyComment;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;

import java.util.List;
import java.util.Map;

public class ReplyHandlerStore extends HandlerStore<ReplyComment> {
  private static ReplyHandlerStore replyCommentSessionStore;
  private UserBasicInfoDao userBasicInfoDao;
  private CommentDao commentDao;

  private ReplyHandlerStore() {
    super();
  }

  @Override
  protected void init() {
    commentDao = db.getCommentDao();
    userBasicInfoDao = db.getUserBasicInfoDao();
  }

  @Override
  protected void cleanOrphanOnCreate() {
    commentDao.deleteAllOrphanByOrderedReplyComment();
    RegistryDao registryDao = DecadeDatabase
            .getInstance()
            .getRegistryDao();
    List<AccessRegistry> rs = registryDao.findAllOrphanByAlias("ReplyComment");
    for (AccessRegistry r : rs) {
      String commentId = r.getItemId();
      commentDao.deleteReplyById(commentId);
    }
    registryDao.deleteListRegistry(rs);
  }

  @Override
  protected void createInLocal(Map<String, Object> itemPack) {
    UserBasicInfo userBasicInfo = (UserBasicInfo) itemPack.get("user basic info");
    ReplyComment comment = (ReplyComment) itemPack.get("comment");
    db.runInTransaction(() -> {
      int userId = (int) userBasicInfoDao.insert(userBasicInfo);
      comment.setUserInfoId(userId);
      commentDao.insertReplyComment(comment);
    });

  }

  @Override
  protected ReplyComment loadFromLocal(String itemId) {
    return commentDao.findReplyCommentById(itemId);
  }


  public static ReplyHandlerStore getInstance() {
    if (replyCommentSessionStore == null) {
      replyCommentSessionStore = new ReplyHandlerStore();
    }
    return replyCommentSessionStore;
  }
}
