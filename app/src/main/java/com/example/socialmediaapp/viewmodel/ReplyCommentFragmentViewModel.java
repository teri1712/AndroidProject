package com.example.socialmediaapp.viewmodel;

import android.os.Bundle;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.ReplyCommentSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReplyCommentFragmentViewModel extends ViewModel {

    private Repository<ReplyComment> commentRepository;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private MediatorLiveData<Boolean> loadCommentState;
    private boolean paused;
    private MutableLiveData<Integer> countLoaded;

    public ReplyCommentFragmentViewModel(DataAccessHandler<ReplyComment> dataAccessHandler) {
        super();
        commentRepository = new Repository<>(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();
        loadCommentState = new MediatorLiveData<>();
        countLoaded = new MutableLiveData<>(0);
        paused = false;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public LiveData<SessionHandler> createReplyCommentSession(ReplyComment comment) {
        ReplyCommentSessionHandler commentSessionHandler = new ReplyCommentSessionHandler(comment);
        MutableLiveData<Integer> commentSessionId = sessionRegistry.bindSession(commentSessionHandler);
        return Transformations.switchMap(commentSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return ApplicationContainer.getInstance().sessionRepository.getSessionById(input);
            }
        });
    }

    public MutableLiveData<Integer> getCountLoaded() {
        return countLoaded;
    }

    public LiveData<String> uploadComment(Bundle data) {
        return commentRepository.uploadNewItem(data);
    }

    public void load(int cnt) {
        if (loadCommentState.getValue() || paused) return;
        loadCommentState.setValue(true);
        LiveData<String> callBack = commentRepository.loadNewItems(cnt);
        loadCommentState.addSource(callBack, s -> {
            loadCommentState.removeSource(callBack);
            loadCommentState.setValue(false);
        });
    }

    public Repository<ReplyComment> getCommentRepository() {
        return commentRepository;
    }

    public void loadEntrance() {
        load(1);
    }

    public MutableLiveData<Boolean> getLoadCommentState() {
        return loadCommentState;
    }
}
