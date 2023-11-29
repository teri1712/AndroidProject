package com.example.socialmediaapp.viewmodel;

import android.net.Uri;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;

public class MainCommentFragmentViewModel extends ViewModel {
    private SavedStateHandle savedStateHandle;
    private MutableLiveData<Uri> image;
    private MutableLiveData<String> commentContent;
    private MediatorLiveData<Integer> cntEditedContent;
    private LiveData<String> sessionState;
    private MutableLiveData<Integer> countLike;
    private LiveData<SessionHandler> commentFragmentSession;
    private MediatorLiveData<String> sendState;

    public MainCommentFragmentViewModel(SavedStateHandle savedStateHandle) {
        super();
        this.savedStateHandle = savedStateHandle;
        commentContent = savedStateHandle.getLiveData("comment content");
        image = savedStateHandle.getLiveData("image content");
        countLike = savedStateHandle.getLiveData("count like");
        sendState = new MediatorLiveData<>();
        sendState.setValue("Idle");

        cntEditedContent = new MediatorLiveData<>();
        cntEditedContent.setValue(0);
        cntEditedContent.addSource(commentContent, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                int cur = cntEditedContent.getValue();
                if (s.isEmpty()) {
                    cur ^= cntEditedContent.getValue() & 1;
                } else {
                    cur |= 1;
                }
                cntEditedContent.setValue(cur);
            }
        });
        cntEditedContent.addSource(image, new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                int cur = cntEditedContent.getValue();
                if (uri == null) {
                    cur ^= cntEditedContent.getValue() & 2;
                } else {
                    cur |= 2;
                }
                cntEditedContent.setValue(cur);
            }
        });
    }

    private SessionHandler.SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;

    public void setSessionId(Integer sessionId) {

        commentFragmentSession = sessionRepository.getSessionById(sessionId);
        sessionState = Transformations.switchMap(commentFragmentSession, new Function<SessionHandler, LiveData<String>>() {
            @Override
            public LiveData<String> apply(SessionHandler input) {
                return input.getSessionState();
            }
        });
    }

    public MutableLiveData<Integer> getCountLike() {
        return countLike;
    }

    public LiveData<SessionHandler> getCommentFragmentSession() {
        return commentFragmentSession;
    }

    public MediatorLiveData<String> getSendState() {
        return sendState;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }

    public MutableLiveData<Uri> getImage() {
        return image;
    }

    public MutableLiveData<String> getCommentContent() {
        return commentContent;
    }

    public MediatorLiveData<Integer> getCntEditedContent() {
        return cntEditedContent;
    }

}
