package com.example.socialmediaapp.application.session.helper;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.socialmediaapp.apis.UserApi;
import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.SearchItemDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.dao.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.SearchItem;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RecentSearchAccessHelper extends DataAccessHelper<UserBasicInfo> {
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private AppDatabase db = ApplicationContainer.getInstance().database;
   private UserBasicInfoDao userBasicInfoDao;
   private SequenceDao sequenceDao;
   private SearchItemDao searchItemDao;
   private DtoConverter dtoConverter;

   public RecentSearchAccessHelper() {
      userBasicInfoDao = db.getUserBasicInfoDao();
      searchItemDao = db.getSearchItemDao();
      sequenceDao = db.getSequenceDao();
      dtoConverter = new DtoConverter(ApplicationContainer.getInstance());
   }

   @Override
   public List<UserBasicInfo> loadFromLocalStorage(HashMap<String, Object> query) {
      List<UserBasicInfo> result = new ArrayList<>();
      UserBasicInfo lastItem = (UserBasicInfo) query.get("last item");
      int length = (int) query.get("length");
      List<SearchItem> searchItems = searchItemDao.loadRecentSearchItems(lastItem.getAlias(), length);
      for (SearchItem searchItem : searchItems) {
         com.example.socialmediaapp.application.entity.UserBasicInfo user = userBasicInfoDao.findUserBasicInfo(searchItem.getUserInfoId());

         UserBasicInfo userBasicInfo = new UserBasicInfo();
         userBasicInfo.setFullname(user.getFullname());
         userBasicInfo.setAlias(user.getAlias());
         userBasicInfo.setAvatar(BitmapFactory.decodeFile(user.getAvatarUri()));
         result.add(userBasicInfo);

      }
      return result;
   }

   @Override
   public Bundle loadFromServer() throws IOException {
      Bundle result = new Bundle();
      Integer uId = searchItemDao.findLastSearchItem().getUserInfoId();
      com.example.socialmediaapp.application.entity.UserBasicInfo uzer = userBasicInfoDao.findUserBasicInfo(uId);
      Call<List<UserBasicInfoBody>> req = retrofit.create(UserApi.class).loadRecentSearch(uzer.getAlias());
      Response<List<UserBasicInfoBody>> res = req.execute();
      List<UserBasicInfoBody> users = res.body();
      List<com.example.socialmediaapp.application.entity.UserBasicInfo> batch = new ArrayList<>();
      for (UserBasicInfoBody u : users) {
         batch.add(dtoConverter.convertToUserBasicInfo(u, session.getId()));
      }
      db.runInTransaction(() -> {
         for (com.example.socialmediaapp.application.entity.UserBasicInfo userBasicInfo : batch) {
            int userId = (int) userBasicInfoDao.insert(userBasicInfo);

            SearchItem searchItem = new SearchItem();
            searchItem.setOrd(sequenceDao.getTailValue());
            searchItem.setUserInfoId(userId);
            searchItemDao.insert(searchItem);
         }
      });
      result.putInt("count loaded", batch.size());
      return result;
   }

   @Override
   public UserBasicInfo uploadToServer(Bundle query) throws IOException, FileNotFoundException {
      String userAlias = query.getString("user alias");
      Call<UserBasicInfoBody> req = retrofit.create(UserApi.class).addToRecentSearch(userAlias);
      Response<UserBasicInfoBody> res = req.execute();
      com.example.socialmediaapp.application.entity.UserBasicInfo u = dtoConverter.convertToUserBasicInfo(res.body(), session.getId());
      db.runInTransaction(() -> {
         SearchItem oldItem = searchItemDao.findByAlias(userAlias);
         if (oldItem != null) {
            userBasicInfoDao.deleteById(oldItem.getUserInfoId());
         }

         int userId = (int) userBasicInfoDao.insert(u);

         SearchItem searchItem = new SearchItem();
         searchItem.setOrd(sequenceDao.getTailValue());
         searchItem.setUserInfoId(userId);
         searchItemDao.insert(searchItem);
      });
      return dtoConverter.convertToModelUserBasicInfo(res.body());
   }

   public String deleteRecentSearchItem(String userAlias) throws IOException {
      Call<ResponseBody> req = retrofit.create(UserApi.class).deleteRecentSearch(userAlias);
      Response<ResponseBody> res = req.execute();
      if (res.code() == 200) {
         db.runInTransaction(() -> {
            SearchItem oldItem = searchItemDao.findByAlias(userAlias);
            userBasicInfoDao.deleteById(oldItem.getUserInfoId());
         });
      }
      return res.code() == 200 ? "Success" : " Failed";
   }

   @Override
   public void cleanAll() {
      List<SearchItem> items = searchItemDao.findAll();
      for (SearchItem item : items) {
         userBasicInfoDao.deleteById(item.getUserInfoId());
      }
      for (String fn : dtoConverter.getCachedFiles()) {
         File file = new File(fn);
         file.delete();
      }
   }

}
