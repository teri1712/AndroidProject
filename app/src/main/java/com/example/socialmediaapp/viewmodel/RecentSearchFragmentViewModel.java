package com.example.socialmediaapp.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.repo.RecentSearchRepo;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.util.HashMap;
import java.util.List;

public class RecentSearchFragmentViewModel extends ViewModel {

    private RecentSearchRepo itemRepository;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private MediatorLiveData<Update<UserBasicInfo>> itemUpdate;
    private MutableLiveData<Boolean> loadItem;

    public RecentSearchFragmentViewModel(RecentSearchAccessHandler dataAccessHandler) {
        super();
        itemRepository = new RecentSearchRepo(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();
        itemUpdate = new MediatorLiveData<>();
        loadItem = new MutableLiveData<>();
    }

    public MediatorLiveData<Update<UserBasicInfo>> getItemUpdate() {
        return itemUpdate;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public void loadItems() {
        Bundle query = new Bundle();
        MutableLiveData<List<UserBasicInfo>> callBack = itemRepository.fetchNewItems(query);
        itemUpdate.addSource(callBack, new Observer<List<UserBasicInfo>>() {
            @Override
            public void onChanged(List<UserBasicInfo> items) {
                for (UserBasicInfo item : items) {
                    itemUpdate.setValue(new Update<>(Update.Op.ADD, item, -1));
                }
                itemUpdate.removeSource(callBack);
            }
        });
    }


    public void onClickToUserProfile(String who) {
        Bundle data = new Bundle();
        data.putString("user alias", who);
        LiveData<HashMap<String, Object>> callBack = itemRepository.uploadNewItem(data);
        itemUpdate.addSource(callBack, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                String status = (String) hashMap.get("status");
                UserBasicInfo item = (UserBasicInfo) hashMap.get("item");
                if (status.equals("Success")) {
                    itemUpdate.setValue(new Update<>(Update.Op.ADD, item, 0));
                }
                itemUpdate.removeSource(callBack);
            }
        });
    }

    public void deleteRecentSearchItem(String who, int posInParent) {
        itemUpdate.postValue(new Update<>(Update.Op.REMOVE, null, posInParent));
        LiveData<String> callBack = itemRepository.deleteItem(who);
    }
}
