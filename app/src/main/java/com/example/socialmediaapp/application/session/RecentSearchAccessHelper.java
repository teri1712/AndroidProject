package com.example.socialmediaapp.application.session;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.socialmediaapp.api.UserApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.SearchItemDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.SearchItem;
import com.example.socialmediaapp.application.entity.user.UserBasicInfo;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class RecentSearchAccessHelper extends DataAccessHelper<UserBasicInfoModel> {
  private final DecadeDatabase db;
  private final UserBasicInfoDao userDao;
  private final SequenceDao seqDao;
  private final SearchItemDao dao;

  public RecentSearchAccessHelper() {
    super("Recent search");
    db = DecadeDatabase.getInstance();
    userDao = db.getUserBasicInfoDao();
    dao = db.getSearchItemDao();
    seqDao = db.getSequenceDao();
  }

  @Override
  public List<UserBasicInfoModel> loadFromLocal(Map<String, Object> query) {
    List<UserBasicInfoModel> result = new ArrayList<>();
    UserBasicInfoModel lastItem = (UserBasicInfoModel) query.get("last item");
    int length = (int) query.get("length");
    List<SearchItem> searchItems = dao.loadRecentSearchItems(lastItem == null ? null : lastItem.getId(), length);
    for (SearchItem searchItem : searchItems) {
      UserBasicInfo user = userDao.findUser(searchItem.getUserInfoId());
      result.add(ModelConvertor.convertToUserModel(user));
    }
    return result;
  }

  @Override
  public Bundle loadFromServer() throws IOException {
    Bundle result = new Bundle();
    SearchItem last = dao.findLastSearchItem();
    String id = last == null ? null : userDao.findUser(last.getUserInfoId()).getId();
    Call<List<UserBasicInfoBody>> req = HttpCallSupporter
            .create(UserApi.class)
            .loadRecentSearch(id);
    Response<List<UserBasicInfoBody>> res = req.execute();
    List<UserBasicInfoBody> users = res.body();
    List<UserBasicInfo> batch = new ArrayList<>();
    for (UserBasicInfoBody u : users) {
      UserBasicInfo userBasicInfo = DtoConverter.convertToUserBasicInfo(u);
      SearchItem searchItem = new SearchItem();
      searchItem.setId(seqDao.getTailValue());
      searchItem.setUserInfoId((int) userDao.insert(userBasicInfo));
      dao.insert(searchItem);
    }
    assert users.size() <= 1;
    result.putInt("count loaded", batch.size());
    return result;
  }

  public String deleteRecentSearchItem(String userAlias) throws IOException {
    Call<ResponseBody> req = HttpCallSupporter.create(UserApi.class).deleteRecentSearch(userAlias);
    Response<ResponseBody> res = req.execute();
    db.runInTransaction(() -> {
      SearchItem oldItem = dao.findByUserId(userAlias);
      userDao.deleteById(oldItem.getUserInfoId());
    });
    return res.code() == 200 ? "Success" : " Failed";
  }

  @Override
  public void cleanAll() {
  }

}
