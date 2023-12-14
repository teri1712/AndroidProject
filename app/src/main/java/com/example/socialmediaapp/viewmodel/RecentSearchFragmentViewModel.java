package com.example.socialmediaapp.viewmodel;

import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.repo.RecentSearchRepository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecentSearchFragmentViewModel extends ViewModel {
    private RecentSearchRepository itemRepository;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private MediatorLiveData<Boolean> loadItemState;
    private boolean paused;

    public RecentSearchFragmentViewModel(RecentSearchAccessHandler dataAccessHandler) {
        super();
        itemRepository = new RecentSearchRepository(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();

        loadItemState = new MediatorLiveData<>();
        paused = false;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }


    public void load(int cnt) {
        if (loadItemState.getValue() || paused) return;
        loadItemState.setValue(true);

        LiveData<String> callBack = itemRepository.loadNewItems(cnt);
        loadItemState.addSource(callBack, s -> {
            loadItemState.removeSource(callBack);
            loadItemState.setValue(false);
        });
    }

    public void loadEntrance() {
        load(5);
    }

    public LiveData<String> onClickToUserProfile(String who) {

        MutableLiveData<String> res = new MutableLiveData<>();

        Bundle data = new Bundle();
        data.putString("user alias", who);
        return itemRepository.uploadNewItem(data);
    }

    public MutableLiveData<Boolean> getLoadItemState() {
        return loadItemState;
    }

    public RecentSearchRepository getItemRepository() {
        return itemRepository;
    }
}
