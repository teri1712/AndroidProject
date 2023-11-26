package com.example.socialmediaapp.viewmodel.refactor;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.container.session.helper.PostAccessHelper;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;

import java.util.List;

public class PostFragmentViewModel extends ViewModel {

    private Repository<Post> postRepository;
    private SessionHandler.SessionRegistry sessionRegistry;

    private MutableLiveData<String> sessionState;
    private SessionHandler sessionHandler;

    public PostFragmentViewModel(DataAccessHandler<Post> dataAccessHandler) {
        super();
        sessionHandler = dataAccessHandler;
        postRepository = new Repository<>(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();
    }

    public Repository<Post> getPostRepository() {
        return postRepository;
    }

    public SessionHandler getSessionHandler() {
        return sessionHandler;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public SessionHandler.SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }
}
