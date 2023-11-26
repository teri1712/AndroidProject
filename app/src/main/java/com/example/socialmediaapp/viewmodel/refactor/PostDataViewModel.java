package com.example.socialmediaapp.viewmodel.refactor;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.container.session.PostSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.container.session.helper.CommentAccessHelper;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class PostDataViewModel extends DataViewModel<Post> {
    private PostSessionHandler postSessionHandler;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<Integer> viewCommentSessionId;
    private MutableLiveData<String> sessionState;
    private LiveData<Integer> countLike, countComment, countShare;
    private MediatorLiveData<Boolean> isLiked;
    private MediatorLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;

    public PostDataViewModel(PostSessionHandler postSessionHandler, Post data) {
        super();
        this.postSessionHandler = postSessionHandler;
        isLiked = new MediatorLiveData<>();
        isLiked.setValue(data.isLiked());
        time = new MediatorLiveData<>();
        time.setValue(data.getTime());
        sessionRegistry = postSessionHandler.getSessionRegistry();
        sessionState = postSessionHandler.getSessionState();
        countLikeContent = new MediatorLiveData<>();
        countLikeContent.setValue("");
        initSessionHandler();
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

    public MutableLiveData<String> getSessionState() {
        return sessionState;
    }

    private void initSessionHandler() {

        liveData = postSessionHandler.getDataSyncEmitter();

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

    public SessionHandler.SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    public PostSessionHandler getPostSessionHandler() {
        return postSessionHandler;
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

    public MutableLiveData<Integer> getViewCommentSessionId() {
        if (viewCommentSessionId == null)
            viewCommentSessionId = sessionRegistry.register(new DataAccessHandler<Comment>(new CommentAccessHelper(liveData.getValue().getId())));
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
