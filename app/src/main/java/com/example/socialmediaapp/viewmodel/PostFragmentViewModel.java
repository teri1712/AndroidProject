package com.example.socialmediaapp.viewmodel;

import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.HashMap;

public class PostFragmentViewModel extends ViewModel {

    private Repository<Post> postRepository;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private MediatorLiveData<Boolean> loadPostState;
    private boolean paused;

    public PostFragmentViewModel(DataAccessHandler<Post> dataAccessHandler) {
        postRepository = new Repository<>(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();
        loadPostState = new MediatorLiveData<>();
        paused = false;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public LiveData<SessionHandler> createPostSession(Post post) {
        PostSessionHandler postSessionHandler = new PostSessionHandler(post);
        MutableLiveData<Integer> postSessionId = sessionRegistry.bindSession(postSessionHandler);
        return Transformations.switchMap(postSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return ApplicationContainer.getInstance().sessionRepository.getSessionById(input);
            }
        });
    }

    public LiveData<HashMap<String, Object>> uploadPost(Bundle data) {
        LiveData<String> result = postRepository.uploadNewItem(data);
        MediatorLiveData<HashMap<String, Object>> callBack = new MediatorLiveData<>();
        callBack.addSource(result, s -> {
            HashMap<String, Object> m = new HashMap<>();
            m.put("status", s);
            m.put("item", postRepository.get(0));
            callBack.setValue(m);
        });
        return callBack;
    }

    public void load(int cnt) {
        if (loadPostState.getValue() || paused) return;
        loadPostState.setValue(true);

        LiveData<String> callBack = postRepository.loadNewItems(cnt);
        loadPostState.addSource(callBack, length -> {
            loadPostState.setValue(false);
        });
    }

    public void loadEntrance() {
        load(5);
    }

    public MutableLiveData<Boolean> getLoadPostState() {
        return loadPostState;
    }

    public LiveData<String> recycle() {
        return postRepository.recycle();
    }

    public Repository<Post> getPostRepository() {
        return postRepository;
    }
}
