package com.example.socialmediaapp.viewmodel.models;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.application.session.helper.PostAccessHelper;
import com.example.socialmediaapp.viewmodel.PostDataViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;

public class MainPostFragmentViewModel extends ViewModel {

    private UserSessionHandler userSessionHandler;

    public MainPostFragmentViewModel(UserSessionHandler userSessionHandler) {
        super();
        this.userSessionHandler = userSessionHandler;
    }

    public LiveData<SessionHandler> recyclePostFragment() {
        SessionHandler.SessionRegistry sessionRegistry = userSessionHandler.getSessionRegistry();
        sessionRegistry.unBindSession(userSessionHandler.getPostDataAccessSessionId());
        DataAccessHandler<Post> postDataAccessHandler = new DataAccessHandler<>(Post.class, new PostAccessHelper());
        MutableLiveData<Integer> newSessionId = sessionRegistry.bindSession(postDataAccessHandler);
        LiveData<SessionHandler> newPostSession = Transformations.switchMap(newSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return ApplicationContainer.getInstance().sessionRepository.getSessionById(input);
            }
        });
        return newPostSession;
    }

}
