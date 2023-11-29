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
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.HashMap;
import java.util.List;

public class CommentFragmentViewModel extends ViewModel {

    private Repository<Comment> commentRepository;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private MediatorLiveData<Update<Comment>> commentUpdate;
    private MutableLiveData<Boolean> loadCommentState;

    public CommentFragmentViewModel(DataAccessHandler<Comment> dataAccessHandler) {
        super();
        commentRepository = new Repository<>(dataAccessHandler);
        sessionRegistry = dataAccessHandler.getSessionRegistry();
        sessionState = dataAccessHandler.getSessionState();
        commentUpdate = new MediatorLiveData<>();
        loadCommentState = new MutableLiveData<>();
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public LiveData<SessionHandler> createCommentSession(Comment comment) {
        CommentSessionHandler commentSessionHandler = new CommentSessionHandler(comment);
        MutableLiveData<Integer> commentSessionId = sessionRegistry.bindSession(commentSessionHandler);
        return Transformations.switchMap(commentSessionId, new Function<Integer, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(Integer input) {
                return ApplicationContainer.getInstance().sessionRepository.getSessionById(input);
            }
        });
    }

    public void deleteCommentSession(int commentSessionId, int posInParent) {
        sessionRegistry.unBindSession(commentSessionId);
        commentUpdate.postValue(new Update<>(Update.Op.REMOVE, null, posInParent));
    }

    public LiveData<String> uploadComment(Bundle data) {
        LiveData<HashMap<String, Object>> result = commentRepository.uploadNewItem(data);
        LiveData<String> callBack = Transformations.map(result, new Function<HashMap<String, Object>, String>() {
            @Override
            public String apply(HashMap<String, Object> input) {
                return (String) input.get("status");
            }
        });
        commentUpdate.addSource(result, new Observer<HashMap<String, Object>>() {
            @Override
            public void onChanged(HashMap<String, Object> hashMap) {
                String status = (String) hashMap.get("status");
                Comment item = (Comment) hashMap.get("item");

                if (status.equals("Success")) {
                    commentUpdate.setValue(new Update<>(Update.Op.ADD, item, 0));
                }
                commentUpdate.removeSource(result);
            }
        });

        return callBack;
    }

    public void loadComments() {
        loadCommentState.setValue(true);
        Bundle query = new Bundle();
        MutableLiveData<List<Comment>> callBack = commentRepository.fetchNewItems(query);
        commentUpdate.addSource(callBack, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                for (Comment c : comments) {
                    commentUpdate.setValue(new Update<>(Update.Op.ADD, c, -1));
                }
                commentUpdate.removeSource(callBack);
                loadCommentState.setValue(false);
            }
        });
    }

    public MutableLiveData<Boolean> getLoadCommentState() {
        return loadCommentState;
    }

    public MediatorLiveData<Update<Comment>> getCommentUpdate() {
        return commentUpdate;
    }
}
