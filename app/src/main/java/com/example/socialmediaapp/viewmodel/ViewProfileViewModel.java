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
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

public class ViewProfileViewModel extends DataViewModel<UserProfile> {
    private LiveData<String> sessionState;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<SessionHandler> viewProfileSessionHandler;
    private Integer sessionId;
    private SessionHandler.SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
    private LiveData<Integer> viewAvatarSessionId, viewBackgroundSessionId;
    private LiveData<SessionHandler> userPostsSession;

    public ViewProfileViewModel(Integer sessionId) {
        super();
        this.sessionId = sessionId;
        this.viewProfileSessionHandler = sessionRepository.getSessionById(sessionId);
        sessionState = Transformations.switchMap(viewProfileSessionHandler, new Function<SessionHandler, LiveData<String>>() {
            @Override
            public LiveData<String> apply(SessionHandler input) {
                return input.getSessionState();
            }
        });
        liveData = (MutableLiveData<UserProfile>) Transformations.switchMap(viewProfileSessionHandler, new Function<SessionHandler, LiveData<UserProfile>>() {
            @Override
            public LiveData<UserProfile> apply(SessionHandler input) {
                sessionRegistry = input.getSessionRegistry();
                return ((ViewProfileSessionHandler) input).getDataSyncEmitter();
            }
        });
        LiveData<SessionHandler> postDataAccess = Transformations.switchMap(viewProfileSessionHandler, new Function<SessionHandler, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(SessionHandler input) {
                return sessionRepository.getSessionById(((ViewProfileSessionHandler) input).getPostRepositorySessionId());
            }
        });
        viewAvatarSessionId = Transformations.switchMap(postDataAccess, new Function<SessionHandler, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(SessionHandler input) {
                ViewProfileSessionHandler vpsh = (ViewProfileSessionHandler) input;
                return vpsh.getAvatarPostSessionId();
            }
        });
        viewBackgroundSessionId = Transformations.switchMap(postDataAccess, new Function<SessionHandler, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(SessionHandler input) {
                ViewProfileSessionHandler vpsh = (ViewProfileSessionHandler) input;
                return vpsh.getBackgroundPostSessionId();
            }
        });
        userPostsSession = Transformations.switchMap(postDataAccess, new Function<SessionHandler, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(SessionHandler input) {
                ViewProfileSessionHandler vpsh = (ViewProfileSessionHandler) input;
                return sessionRepository.getSessionById(vpsh.getPostRepositorySessionId());
            }
        });
    }


    public MutableLiveData<SessionHandler> getViewProfileSessionHandler() {
        return viewProfileSessionHandler;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }

    public LiveData<Integer> getViewAvatarSessionId() {
        return viewAvatarSessionId;
    }

    public LiveData<SessionHandler> getUserPostsSession() {
        return userPostsSession;
    }

    public LiveData<Integer> getViewBackgroundSessionId() {
        return viewBackgroundSessionId;
    }

}
