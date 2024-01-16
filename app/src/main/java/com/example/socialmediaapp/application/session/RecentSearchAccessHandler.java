package com.example.socialmediaapp.application.session;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.io.IOException;

public class RecentSearchAccessHandler extends DataAccessHandler<UserBasicInfoModel> {
  public RecentSearchAccessHandler() {
    super(new RecentSearchAccessHelper());
  }

  public

  public LiveData<String> deleteItem(String userAlias) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    post(() -> {
      postTask(() -> {
        RecentSearchAccessHelper recentSearchAccessHelper = (RecentSearchAccessHelper) dataAccessHelper;
        String result = null;
        try {
          result = recentSearchAccessHelper.deleteRecentSearchItem(userAlias);
        } catch (IOException e) {
          result = "Failed";
          e.printStackTrace();
        }
        callBack.postValue(result);
      });
    });
    return callBack;
  }
}
