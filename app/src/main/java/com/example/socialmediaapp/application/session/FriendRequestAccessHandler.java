package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.api.entities.FriendRequestBody;
import com.example.socialmediaapp.application.dao.user.FriendDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.user.FriendRequestModel;

public class FriendRequestAccessHandler
        extends FcmAccessHandler<FriendRequestModel, FriendRequestBody> {
  private final UserBasicInfoDao userDao;
  private final FriendDao friendDao;
  private final DecadeDatabase db;

  public FriendRequestAccessHandler(FriendRequestAccessHelper accessHelper) {
    super(accessHelper, accessHelper);
    db = DecadeDatabase.getInstance();
    userDao = db.getUserBasicInfoDao();
    friendDao = db.getFriendDao();
  }

  protected void delete(String userId) {
    db.runInTransaction(() -> {
      FriendRequestItem oldItem = friendDao.findByUserId(userId);
      userDao.deleteById(oldItem.getUserInfoId());
    });
    post(new Runnable() {
      @Override
      public void run() {

      }
    });
  }
}
