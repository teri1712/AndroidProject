package com.example.socialmediaapp.viewmodel;

import android.se.omapi.Session;
import android.widget.ListView;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.Comment;

public class CommentDataViewModel extends DataViewModel<Comment> {
    private LiveData<SessionHandler> commentSessionHandler;
    private LiveData<Integer> countLike, countComment;
    private MutableLiveData<Boolean> likeLiveData;
    private MutableLiveData<String> time;
    private LiveData<String> sessionState;
    private LiveData<SessionHandler.SessionRegistry> sessionRegistry;
    private LiveData<SessionHandler> replyCommentsSession;
    private MediatorLiveData<String> countLikeContent;

    public CommentDataViewModel(LiveData<SessionHandler> commentSessionHandler, Comment data) {
        super();
        this.commentSessionHandler = commentSessionHandler;
        sessionState = Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<String>>() {
            @Override
            public LiveData<String> apply(SessionHandler input) {
                return input.getSessionState();
            }
        });
        sessionRegistry = Transformations.map(commentSessionHandler, new Function<SessionHandler, SessionHandler.SessionRegistry>() {
            @Override
            public SessionHandler.SessionRegistry apply(SessionHandler input) {
                return input.getSessionRegistry();
            }
        });
        replyCommentsSession = Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<SessionHandler>>() {
            @Override
            public LiveData<SessionHandler> apply(SessionHandler input) {
                SessionHandler.SessionRepository sessionRepository = ApplicationContainer.getInstance().sessionRepository;
                return sessionRepository.getSessionById(((CommentSessionHandler) input).getReplyCommentSessionId());
            }
        });
        liveData = (MutableLiveData<Comment>) Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<Comment>>() {
            @Override
            public LiveData<Comment> apply(SessionHandler input) {
                return ((CommentSessionHandler) input).getDataSyncEmitter();
            }
        });
        time = new MutableLiveData<>();
        time.setValue(data.getTime());

        initLikePanelView();
        countLikeContent = new MediatorLiveData<>();

        countLikeContent.addSource(countLike, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null || likeLiveData.getValue() == null) return;
                changeCountLike(likeLiveData.getValue(), integer);
            }
        });
        countLikeContent.addSource(likeLiveData, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b == null || countLike.getValue() == null) return;
                changeCountLike(b, countLike.getValue());
            }
        });

    }

    private void changeCountLike(boolean b, int integer) {
        int value = integer + (b ? 1 : 0);
        countLikeContent.setValue(value == 0 ? "" : Integer.toString(value));
    }

    public MutableLiveData<Boolean> getLikeLiveData() {
        return likeLiveData;
    }

    private void initLikePanelView() {
        countLike = Transformations.map(liveData, new Function<Comment, Integer>() {
            @Override
            public Integer apply(Comment input) {
                return input.getCountLike();
            }
        });
        countComment = Transformations.map(liveData, new Function<Comment, Integer>() {
            @Override
            public Integer apply(Comment input) {
                return input.getCountComment();
            }
        });
        likeLiveData = (MediatorLiveData<Boolean>) Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(SessionHandler input) {
                return ((CommentSessionHandler) input).getLikeSync();
            }
        });
    }

    public LiveData<SessionHandler> getCommentSessionHandler() {
        return commentSessionHandler;
    }

    public LiveData<SessionHandler> getReplyCommentsSession() {
        return replyCommentsSession;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }


    public MutableLiveData<String> getTime() {
        return time;
    }

    public MediatorLiveData<String> getCountLikeContent() {
        return countLikeContent;
    }

    public LiveData<Integer> getCountComment() {
        return countComment;
    }

}
