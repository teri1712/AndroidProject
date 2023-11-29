package com.example.socialmediaapp.application.session;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.helper.DataAccessHelper;
import com.example.socialmediaapp.application.session.helper.RecentSearchAccessHelper;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.io.IOException;

public class RecentSearchAccessHandler extends DataAccessHandler<UserBasicInfo> {
    public RecentSearchAccessHandler(Class<UserBasicInfo> t) {
        super(t, new RecentSearchAccessHelper());
    }

    public MutableLiveData<String> deleteItem(String userAlias) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        post(() -> postToWorker(() -> {
            RecentSearchAccessHelper recentSearchAccessHelper = (RecentSearchAccessHelper) dataAccessHelper;
            try {
                callBack.postValue(recentSearchAccessHelper.deleteRecentSearchItem(userAlias));
            } catch (IOException e) {
                callBack.postValue("Failed");
                e.printStackTrace();
            }
        }));
        return callBack;
    }
}
