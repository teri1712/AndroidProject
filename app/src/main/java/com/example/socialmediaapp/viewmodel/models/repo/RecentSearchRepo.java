package com.example.socialmediaapp.viewmodel.models.repo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class RecentSearchRepo extends Repository<UserBasicInfo> {
    public RecentSearchRepo(RecentSearchAccessHandler dataAccessHandler) {
        super(dataAccessHandler);
    }

    public LiveData<String> deleteItem(String userAlias) {
        MediatorLiveData<String> callBack = new MediatorLiveData<>();
        RecentSearchAccessHandler recentSearchAccessHandler = (RecentSearchAccessHandler) dataAccessHandler;
        LiveData<String> res = recentSearchAccessHandler.deleteItem(userAlias);

        callBack.addSource(res, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                callBack.setValue(s);
                callBack.removeSource(res);
            }
        });
        return callBack;
    }
}
