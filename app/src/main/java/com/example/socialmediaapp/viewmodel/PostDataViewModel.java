package com.example.socialmediaapp.viewmodel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;

public class PostDataViewModel extends DataViewModel<Post> {
    private LiveData<SessionHandler> postSessionHandler;
    private LiveData<Integer> viewCommentSessionId;
    private LiveData<String> sessionState;
    private LiveData<Integer> countLike, countComment, countShare;
    private MutableLiveData<Boolean> isLiked;
    private MutableLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;
    private LiveData<Integer> postSessionId;

    public PostDataViewModel(LiveData<SessionHandler> postSessionHandler, Post data) {
        super();
        this.postSessionHandler = postSessionHandler;
        viewCommentSessionId = Transformations.map(postSessionHandler, new Function<SessionHandler, Integer>() {
            @Override
            public Integer apply(SessionHandler input) {
                return ((PostSessionHandler) input).getCommentSessionId();
            }
        });
        postSessionId = Transformations.map(postSessionHandler, new Function<SessionHandler, Integer>() {
            @Override
            public Integer apply(SessionHandler input) {
                return input.getId();
            }
        });
        sessionState = Transformations.switchMap(postSessionHandler, new Function<SessionHandler, LiveData<String>>() {
            @Override
            public LiveData<String> apply(SessionHandler input) {
                return input.getSessionState();
            }
        });
        liveData = (MutableLiveData<Post>) Transformations.switchMap(postSessionHandler, new Function<SessionHandler, LiveData<Post>>() {
            @Override
            public LiveData<Post> apply(SessionHandler input) {
                return ((PostSessionHandler) input).getDataSyncEmitter();
            }
        });
        isLiked = new MutableLiveData<>();
        isLiked.setValue(data.isLiked());
        time = new MutableLiveData<>();
        time.setValue(data.getTime());
        countLikeContent = new MediatorLiveData<>();
        countLikeContent.setValue("");
        initLikePanelView();
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
        liveData.setValue(data);
    }

    public LiveData<Integer> getPostSessionId() {
        return postSessionId;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }

    private void initLikePanelView() {
        countLike = Transformations.map(liveData, new Function<Post, Integer>() {
            @Override
            public Integer apply(Post input) {
                return input.getLikeCount();
            }
        });
        countComment = Transformations.map(liveData, new Function<Post, Integer>() {
            @Override
            public Integer apply(Post input) {
                return input.getCommentCount();
            }
        });
        countShare = Transformations.map(liveData, new Function<Post, Integer>() {
            @Override
            public Integer apply(Post input) {
                return input.getShareCount();
            }
        });
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

    public LiveData<Integer> getViewCommentSessionId() {
        return viewCommentSessionId;
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

    public LiveData<Integer> getCountShare() {
        return countShare;
    }

    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }
}
