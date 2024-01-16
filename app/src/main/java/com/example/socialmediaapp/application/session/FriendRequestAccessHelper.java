package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.api.FriendApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.FriendRequestBody;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.user.FriendDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;
import com.example.socialmediaapp.models.user.FriendRequestModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Response;

public class FriendRequestAccessHelper
        extends DataAccessHelper<FriendRequestModel>
        implements DataUpdateHelper<FriendRequestModel> {
  private final DecadeDatabase db;
  private final UserBasicInfoDao userDao;
  private final FriendDao friendDao;

  public FriendRequestAccessHelper() {
    super("Friend Request");
    db = DecadeDatabase.getInstance();
    userDao = db.getUserBasicInfoDao();
    friendDao = db.getFriendDao();
  }

  private FriendRequestModel flushToLocal(FriendRequestBody body) {
    UserBasicInfo userInfo = DtoConverter.convertToUserBasicInfo(body.getUserBody());
    FriendRequestItem fReqItem = DtoConverter.convertToFReq(body);
    db.runInTransaction(() -> {
      fReqItem.setUserInfoId((int) userDao.insert(userInfo));
      friendDao.insert(fReqItem);
    });
    return ModelConvertor.convertToFReq(fReqItem);
  }

  @Override
  public List<FriendRequestModel> loadFromLocal(Map<String, Object> query) {
    List<FriendRequestModel> result = new ArrayList<>();
    FriendRequestModel lastItem = (FriendRequestModel) query.get("last item");
    int length = (int) query.get("length");
    List<FriendRequestItem> items = friendDao.loadFriendRequestItems(
            lastItem == null ? null : lastItem.getUserModel().getId(),
            length);
    for (FriendRequestItem item : items) {
      result.add(ModelConvertor.convertToFReq(item));
    }

    return result;
  }


  @Override
  public Bundle loadFromServer() throws IOException {
    Bundle result = new Bundle();
    FriendRequestItem last = friendDao.findLastFriendRequestItem();
    String id = last == null ? null : userDao.findUser(last.getUserInfoId()).getId();
    Response<List<FriendRequestBody>> res = HttpCallSupporter
            .create(FriendApi.class)
            .loadFriendRequests(id)
            .execute();
    HttpCallSupporter.debug(res);

    List<FriendRequestBody> bodies = res.body();
    for (FriendRequestBody body : bodies) {
      flushToLocal(body);
    }

    result.putInt("count loaded", bodies.size());
    return result;
  }

  @Override
  public void cleanAll() {
    friendDao.deleteAll();
  }

  @Override
  public List<FriendRequestModel> update(Map<String, Object> data) throws IOException {
    FriendRequestBody body = (FriendRequestBody) data.get("item");
    FriendRequestModel model = flushToLocal(body);
    List<FriendRequestModel> list = new ArrayList<>();
    list.add(model);
    return list;
  }
}
