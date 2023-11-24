package com.example.socialmediaapp.viewmodels.refactor;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.container.session.CommentSessionHandler;
import com.example.socialmediaapp.container.session.PostSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.viewmodels.models.post.Comment;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class CommentDataViewModel extends DataViewModel<Comment> {
    private MutableLiveData<CommentSessionHandler> commentSessionHandler;
    private MediatorLiveData<Integer> countLike, countComment;
    private MediatorLiveData<Boolean> isLiked;
    private MediatorLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;

    public CommentDataViewModel(SessionHandler.SessionRegistry sessionRegistry, Comment data) {
        super(sessionRegistry, data);
        countLike = new MediatorLiveData<>();
        countLike.setValue(data.getCountLike());
        countComment = new MediatorLiveData<>();
        countComment.setValue(data.getCountComment());
        isLiked = new MediatorLiveData<>();
        isLiked.setValue(data.isLiked());
        time = new MediatorLiveData<>();
        time.setValue(data.getTime());

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
        initSessionHandler(sessionRegistry);
    }

    private void initSessionHandler(SessionHandler.SessionRegistry sessionRegistry) {

        CommentSessionHandler sessionHandler = new CommentSessionHandler(data.getId());
        commentSessionHandler = sessionRegistry.register(sessionHandler);
        countLikeContent.addSource(commentSessionHandler, new Observer<CommentSessionHandler>() {
            @Override
            public void onChanged(CommentSessionHandler s) {
                MutableLiveData<CommentSessionHandler.CommentDataSync> dataSync = s.getDataSyncEmitter();

                countLike.addSource(dataSync, new Observer<CommentSessionHandler.CommentDataSync>() {
                    @Override
                    public void onChanged(CommentSessionHandler.CommentDataSync postDataSync) {
                        countLike.setValue(postDataSync.likeCount);
                    }
                });
                countComment.addSource(dataSync, new Observer<CommentSessionHandler.CommentDataSync>() {
                    @Override
                    public void onChanged(CommentSessionHandler.CommentDataSync postDataSync) {
                        countLike.setValue(postDataSync.commentCount);
                    }
                });
                countLikeContent.removeSource(commentSessionHandler);
            }
        });

    }

    public MutableLiveData<CommentSessionHandler> getCommentSessionHandler() {
        return commentSessionHandler;
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
                countLikeContent.setValue(data.getAuthor().getFullname());
            }
        }
    }


    public MutableLiveData<String> getTime() {
        return time;
    }

    public MediatorLiveData<String> getCountLikeContent() {
        return countLikeContent;
    }

    public MutableLiveData<Integer> getCountComment() {
        return countComment;
    }

    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }

}
