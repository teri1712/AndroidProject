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
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class PostDataViewModel extends DataViewModel<Post> {
    private LiveData<SessionHandler> postSessionHandler;
    private LiveData<Integer> viewCommentSessionId;
    private LiveData<String> sessionState;
    private LiveData<Integer> countLike, countComment, countShare;
    private LiveData<Boolean> likeLiveData;
    private LiveData<String> time;
    private MediatorLiveData<String> countLikeContent;
    private LiveData<Integer> postSessionId;
    private MediatorLiveData<Integer> totalCountLike;
    private LiveData<UserBasicInfo> author;
    private LiveData<String> userHostName;

    public PostDataViewModel(LiveData<SessionHandler> postSessionHandler, LiveData<String> fullname) {
        super();
        this.postSessionHandler = postSessionHandler;
        this.userHostName = fullname;
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
        author = Transformations.map(liveData, new Function<Post, UserBasicInfo>() {
            @Override
            public UserBasicInfo apply(Post input) {
                return input.getAuthor();
            }
        });
        time = Transformations.map(liveData, new Function<Post, String>() {
            @Override
            public String apply(Post input) {
                return input.getTime();
            }
        });
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

        totalCountLike = new MediatorLiveData<>();
        totalCountLike.setValue(0);
        totalCountLike.addSource(countLike, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                totalCountLike.setValue(integer);
            }
        });
        totalCountLike.addSource(likeLiveData, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Integer cnt = countLike.getValue();
                if (cnt == null) return;
                cnt += (aBoolean) ? 1 : 0;
                totalCountLike.setValue(cnt);
            }
        });

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
        likeLiveData = Transformations.switchMap(postSessionHandler, new Function<SessionHandler, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(SessionHandler input) {
                return ((PostSessionHandler) input).getLikeSync();
            }
        });
    }

    private void changeCountLike(boolean b, int integer) {
        String pref = b ? "You and " : "";
        String suf = b ? " others" : "";
        if (integer != 0) {
            countLikeContent.setValue(pref + integer + suf);
        } else {
            if (!b) {
                countLikeContent.setValue("");
            } else {
                countLikeContent.addSource(userHostName, s -> {
                    countLikeContent.setValue(s);
                    countLikeContent.removeSource(userHostName);
                });
            }
        }
    }

    public LiveData<Boolean> getLikeLiveData() {
        return likeLiveData;
    }

    public LiveData<Integer> getViewCommentSessionId() {
        return viewCommentSessionId;
    }

    public LiveData<Integer> getTotalCountLike() {
        return totalCountLike;
    }

    public LiveData<String> getTime() {
        return time;
    }

    public LiveData<String> getCountLikeContent() {
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

    public LiveData<UserBasicInfo> getAuthor() {
        return author;
    }
}
