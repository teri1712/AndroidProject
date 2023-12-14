package com.example.socialmediaapp.viewmodel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

public class ViewProfileViewModel extends DataViewModel<UserProfile> {
    private LiveData<String> sessionState;
    private ViewProfileSessionHandler viewProfileSessionHandler;
    private LiveData<SessionHandler> viewAvatarSession, viewBackgroundSession;

    public ViewProfileViewModel(ViewProfileSessionHandler sessionHandler) {
        super();
        this.viewProfileSessionHandler = sessionHandler;
        sessionState = viewProfileSessionHandler.getSessionState();
        liveData = viewProfileSessionHandler.getDataSyncEmitter();
        viewAvatarSession = viewProfileSessionHandler.getAvatarPostSession();
        viewBackgroundSession = viewProfileSessionHandler.getBackgroundPostSession();
    }


    public ViewProfileSessionHandler getViewProfileSessionHandler() {
        return viewProfileSessionHandler;
    }
    public LiveData<String> getSessionState() {
        return sessionState;
    }

    public LiveData<SessionHandler> getViewAvatarSession() {
        return viewAvatarSession;
    }

    public LiveData<SessionHandler> getViewBackgroundSession() {
        return viewBackgroundSession;
    }

}
