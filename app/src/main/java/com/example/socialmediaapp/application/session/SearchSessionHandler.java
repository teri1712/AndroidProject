package com.example.socialmediaapp.application.session;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.UserApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SearchSessionHandler extends SessionHandler {
  private RecentSearchAccessHandler recentHandler;
  private ScheduledExecutorService scheduledExecutor;
  private int searchTurn;

  public SearchSessionHandler() {
    super();
    init();
  }

  @Override
  protected void init() {
    super.init();
    searchTurn = 0;
    scheduledExecutor = DecadeApplication.getInstance().sharedScheduledExecutor;
    recentHandler = new RecentSearchAccessHandler();
  }

  public RecentSearchAccessHandler getRecentHandler() {
    return recentHandler;
  }

  @Override
  protected void invalidate() {
    recentHandler.invalidate();
    super.invalidate();
  }

  public MutableLiveData<List<UserBasicInfoModel>> searchForUser(String query) {
    MutableLiveData<List<UserBasicInfoModel>> callBack = new MutableLiveData<>();
    scheduledExecutor.schedule(new Runnable() {
      final int thisSearchTurn = ++searchTurn;

      @Override
      public void run() {
        if (thisSearchTurn == searchTurn) {
          doSearch(callBack, query);
        }
      }
    }, 200, TimeUnit.MILLISECONDS);
    return callBack;
  }

  private void doSearch(MutableLiveData<List<UserBasicInfoModel>> callBack, String query) {
    Call<List<UserBasicInfoBody>> req = HttpCallSupporter
            .create(UserApi.class)
            .searchForUser(query);
    Response<List<UserBasicInfoBody>> res;
    try {
      res = req.execute();
      List<UserBasicInfoBody> users = res.body();
      List<UserBasicInfoModel> batch = new ArrayList<>();
      for (UserBasicInfoBody u : users) {
        batch.add(ModelConvertor.convertBodyToUserBasicInfoModel(u));
      }
      callBack.postValue(batch);
    } catch (IOException e) {
      e.printStackTrace();
      callBack.postValue(new ArrayList<>());
    }
  }
}
