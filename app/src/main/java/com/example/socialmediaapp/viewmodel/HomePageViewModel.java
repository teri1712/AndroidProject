package com.example.socialmediaapp.viewmodel;

import android.content.Context;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.models.HomePageContent;
import com.example.socialmediaapp.viewmodel.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodel.models.UserSession;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.repo.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;

import java.util.List;

public class HomePageViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<Integer> curFragment;
    private MutableLiveData<UserSession> userSession;
    private MutableLiveData<HomePageContent> homePageContent;
    private MutableLiveData<ImagePost> avatarPost;
    private MutableLiveData<ImagePost> backgroundPost;
    private MutableLiveData<UserInformation> userInfo;
    private MediatorLiveData<Update<UserBasicInfo>> updateOnRecentList;
    private ItemRepository<UserBasicInfo> recentSearchList;

    public HomePageViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        curFragment = new MutableLiveData<>(0);
         homePageContent = new MutableLiveData<>();
        userSession = (MutableLiveData<UserSession>) Transformations.switchMap(homePageContent, new Function<HomePageContent, LiveData<UserSession>>() {
            @Override
            public LiveData<UserSession> apply(HomePageContent input) {
                return new MutableLiveData<UserSession>(input.getUser());
            }
        });
        avatarPost = (MutableLiveData<ImagePost>) Transformations.switchMap(userSession, new Function<UserSession, LiveData<ImagePost>>() {
            @Override
            public MutableLiveData<ImagePost> apply(UserSession input) {
                return new MutableLiveData<>(input.getAvatarPost());
            }
        });
        backgroundPost = (MutableLiveData<ImagePost>) Transformations.switchMap(userSession, new Function<UserSession, LiveData<ImagePost>>() {
            @Override
            public MutableLiveData<ImagePost> apply(UserSession input) {
                return new MutableLiveData<>(input.getBackgroundPost());
            }
        });
        userInfo = (MutableLiveData<UserInformation>) Transformations.switchMap(userSession, new Function<UserSession, LiveData<UserInformation>>() {
            @Override
            public LiveData<UserInformation> apply(UserSession input) {
                return new MutableLiveData<>(input.getUserInfo());
            }
        });
        LiveData<List<UserBasicInfo>> recentSearch = Transformations.switchMap(userSession, new Function<UserSession, LiveData<List<UserBasicInfo>>>() {
            @Override
            public LiveData<List<UserBasicInfo>> apply(UserSession input) {
                return new MutableLiveData<>(input.getRecentSearch());
            }
        });
        recentSearchList = new ItemRepository<>();
        updateOnRecentList = recentSearchList.getUpdateOnRepo();
        recentSearchList.getUpdateOnRepo().addSource(recentSearch, new Observer<List<UserBasicInfo>>() {
            @Override
            public void onChanged(List<UserBasicInfo> userBasicInfos) {
                for (UserBasicInfo u : userBasicInfos) {
                    recentSearchList.addToEnd(u);
                }
                recentSearchList.getUpdateOnRepo().removeSource(recentSearch);
            }
        });
    }

    public MutableLiveData<ImagePost> getAvatarPost() {
        return avatarPost;
    }

    public MutableLiveData<ImagePost> getBackgroundPost() {
        return backgroundPost;
    }

    public MutableLiveData<HomePageContent> getHomePageContent() {
        return homePageContent;
    }

    public MutableLiveData<UserInformation> getUserInfo() {
        return userInfo;
    }

    public ItemRepository<UserBasicInfo> getRecentSearchList() {
        return recentSearchList;
    }

    public void onClickToUserProfile(Context context, final UserBasicInfo who) {
        final MutableLiveData<String> callBack = new MutableLiveData<>();
        updateOnRecentList.addSource(callBack, new Observer<String>() {
            @Override
            public void onChanged(String res) {
                if (res.equals("Success")) {
                    recentSearchList.remove(who);
                    recentSearchList.addToEnd(who);
                }
                updateOnRecentList.removeSource(callBack);
            }
        });
        ServiceApi.onClickOnUser(context, who, callBack);
    }

    public void removeRecentProfileItem(Context context, final UserBasicInfo who) {
        final MutableLiveData<String> listener = new MutableLiveData<>();
        updateOnRecentList.addSource(listener, new Observer<String>() {
            @Override
            public void onChanged(String res) {
                if (res.equals("Success")) {
                    recentSearchList.remove(who);
                }
                updateOnRecentList.removeSource(listener);
            }
        });
        ServiceApi.removeRecentProfileItem(context, who, listener);
    }

    public MutableLiveData<Integer> getCurFragment() {
        return curFragment;
    }

    public void loadHomePageContent(Context context) {
        ServiceApi.loadHomePageContent(context, homePageContent);
    }
}
