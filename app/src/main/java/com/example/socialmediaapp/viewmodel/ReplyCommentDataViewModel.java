package com.example.socialmediaapp.viewmodel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;

import com.example.socialmediaapp.application.session.ReplyCommentSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;

public class ReplyCommentDataViewModel extends DataViewModel<ReplyComment> {
    private LiveData<SessionHandler> commentSessionHandler;
    private LiveData<Integer> countLike;
    private MutableLiveData<Boolean> likeLiveData;
    private MutableLiveData<String> time;
    private LiveData<String> sessionState;
    private LiveData<SessionHandler.SessionRegistry> sessionRegistry;
    private MediatorLiveData<String> countLikeContent;

    public ReplyCommentDataViewModel(LiveData<SessionHandler> commentSessionHandler, ReplyComment data) {
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
        liveData = (MutableLiveData<ReplyComment>) Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<ReplyComment>>() {
            @Override
            public LiveData<ReplyComment> apply(SessionHandler input) {
                return ((ReplyCommentSessionHandler) input).getDataSyncEmitter();
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

    private void initLikePanelView() {
        countLike = Transformations.map(liveData, new Function<ReplyComment, Integer>() {
            @Override
            public Integer apply(ReplyComment input) {
                return input.getCountLike();
            }
        });
        likeLiveData = (MutableLiveData<Boolean>) Transformations.switchMap(commentSessionHandler, new Function<SessionHandler, LiveData<Boolean>>() {
            @Override
            public LiveData<Boolean> apply(SessionHandler input) {
                return ((ReplyCommentSessionHandler) input).getLikeSync();
            }
        });
    }

    public LiveData<SessionHandler> getCommentSessionHandler() {
        return commentSessionHandler;
    }

    public LiveData<String> getSessionState() {
        return sessionState;
    }


    public MutableLiveData<String> getTime() {
        return time;
    }

    public MutableLiveData<Boolean> getLikeLiveData() {
        return likeLiveData;
    }

    public MediatorLiveData<String> getCountLikeContent() {
        return countLikeContent;
    }
}
