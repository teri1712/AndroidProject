package com.example.socialmediaapp.viewmodel;

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
    private MutableLiveData<Boolean> isLiked;
    private MutableLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;
    private LiveData<String> sessionState;
    private LiveData<SessionHandler.SessionRegistry> sessionRegistry;


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
        liveData = (MutableLiveData<Comment>) Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<Comment>>() {
            @Override
            public LiveData<Comment> apply(SessionHandler input) {
                return ((CommentSessionHandler) input).getDataSyncEmitter();
            }
        });

        isLiked = new MutableLiveData<>();
        isLiked.setValue(data.isLiked());
        time = new MutableLiveData<>();
        time.setValue(data.getTime());

        initLikePanelView();
        liveData.setValue(data);


        countLikeContent = new MediatorLiveData<>();
        countLikeContent.setValue("");
        countLikeContent.addSource(countLike, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null || isLiked.getValue() == null) return;
                changeCountLike(isLiked.getValue(), integer);

            }
        });
        countLikeContent.addSource(isLiked, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean b) {
                if (b == null || countLike.getValue() == null) return;
                changeCountLike(b, countLike.getValue());
            }
        });

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
    }

    public LiveData<SessionHandler> getCommentSessionHandler() {
        return commentSessionHandler;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }

    private void changeCountLike(boolean b, int integer) {
        String pref = b ? "You and " : "";
        String suf = b ? " others" : "";
        if (integer != 0) {
            countLikeContent.setValue(pref + Integer.toString(integer) + suf);
        } else {
            if (!b) {
                countLikeContent.setValue("");
            } else {
                countLikeContent.setValue(liveData.getValue().getAuthor().getFullname());
            }
        }
    }

    public MutableLiveData<String> getTime() {
        return time;
    }

    public MediatorLiveData<String> getCountLikeContent() {
        return countLikeContent;
    }

    public LiveData<Integer> getCountLike() {
        return countLike;
    }

    public LiveData<Integer> getCountComment() {
        return countComment;
    }

    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }

}
