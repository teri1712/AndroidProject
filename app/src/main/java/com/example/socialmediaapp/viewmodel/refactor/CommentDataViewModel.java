package com.example.socialmediaapp.viewmodel.refactor;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.container.session.CommentSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.Comment;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class CommentDataViewModel extends DataViewModel<Comment> {
    private CommentSessionHandler commentSessionHandler;
    private LiveData<Integer> countLike, countComment;
    private MediatorLiveData<Boolean> isLiked;
    private MediatorLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;
    private MutableLiveData<String> sessionState;
    private SessionHandler.SessionRegistry sessionRegistry;

    public CommentDataViewModel(CommentSessionHandler commentSessionHandler, Comment data) {
        super();
        isLiked = new MediatorLiveData<>();
        isLiked.setValue(data.isLiked());
        time = new MediatorLiveData<>();
        time.setValue(data.getTime());
        this.commentSessionHandler = commentSessionHandler;
        sessionState = commentSessionHandler.getSessionState();
        sessionRegistry = commentSessionHandler.getSessionRegistry();
        initSessionHandler();
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

    private void initSessionHandler() {
        liveData = commentSessionHandler.getDataSyncEmitter();

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

    public CommentSessionHandler getCommentSessionHandler() {
        return commentSessionHandler;
    }

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    public SessionHandler.SessionRegistry getSessionRegistry() {
        return sessionRegistry;
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
