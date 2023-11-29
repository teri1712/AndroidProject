package com.example.socialmediaapp.viewmodel;

import android.widget.Toast;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SearchSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.util.List;

public class SearchFragmentViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MediatorLiveData<List<UserBasicInfo>> searchResult;
    private MutableLiveData<SessionHandler> searchSessionHandler;
    private LiveData<SessionHandler> recentSearchSessionHandler;
    private SessionHandler.SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
    private LiveData<SessionHandler.SessionRegistry> sessionRegistry;

    public SearchFragmentViewModel(Integer searchSessionId) {
        super();
        searchResult = new MediatorLiveData<>();
        searchSessionHandler = sessionRepository.getSessionById(searchSessionId);
        recentSearchSessionHandler = Transformations.switchMap(searchSessionHandler, new Function<SessionHandler, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(SessionHandler input) {
                return ((SearchSessionHandler) input).getRecentSearchSession();
            }
        });
        sessionRegistry = Transformations.map(searchSessionHandler, new Function<SessionHandler, SessionHandler.SessionRegistry>() {
            @Override
            public SessionHandler.SessionRegistry apply(SessionHandler input) {
                return input.getSessionRegistry();
            }
        });
    }

    public LiveData<SessionHandler> getRecentSearchSessionHandler() {
        return recentSearchSessionHandler;
    }

    public MediatorLiveData<List<UserBasicInfo>> getSearchResult() {
        return searchResult;
    }

    private LiveData<List<UserBasicInfo>> curSource;

    public void searchForUser(String query) {
        SearchSessionHandler ssh = (SearchSessionHandler) searchSessionHandler.getValue();
        final MutableLiveData<List<UserBasicInfo>> callBack = ssh.searchForUsers(query);
        if (curSource != null) {
            searchResult.removeSource(curSource);
            curSource = callBack;
        }
        searchResult.addSource(callBack, new Observer<List<UserBasicInfo>>() {
            @Override
            public void onChanged(List<UserBasicInfo> userBasicInfos) {
                searchResult.setValue(userBasicInfos);
            }
        });
    }

    public MutableLiveData<SessionHandler> getSearchSessionHandler() {
        return searchSessionHandler;
    }
}
