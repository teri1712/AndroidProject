package com.example.socialmediaapp.viewmodels;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;

import java.net.ServerSocket;
import java.util.List;

public class SearchFragmentViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MediatorLiveData<List<UserBasicInfo>> searchResult;
    private MutableLiveData<List<UserBasicInfo>> currentSource;

    public SearchFragmentViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        searchResult = new MediatorLiveData<>();
    }

    public MediatorLiveData<List<UserBasicInfo>> getSearchResult() {
        return searchResult;
    }

    public void loadSearchResult(Context context, String query) {
        searchResult.removeSource(currentSource);
        final MutableLiveData<List<UserBasicInfo>> newSource = new MutableLiveData<>();
        currentSource = newSource;
        searchResult.addSource(currentSource, new Observer<List<UserBasicInfo>>() {
            @Override
            public void onChanged(List<UserBasicInfo> userBasicInfos) {
                if (userBasicInfos == null) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                } else {
                    searchResult.setValue(userBasicInfos);
                }
                searchResult.removeSource(newSource);
                currentSource = null;
            }
        });
        ServiceApi.searchForUser(context, query, currentSource);
    }
}
