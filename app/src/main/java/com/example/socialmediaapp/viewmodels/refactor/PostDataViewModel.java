package com.example.socialmediaapp.viewmodels.refactor;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.container.session.PostSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.Comment;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;
import com.example.socialmediaapp.viewmodels.models.repo.ItemRepository;

import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class PostDataViewModel extends DataViewModel<Post> {
    private MutableLiveData<PostSessionHandler> postSessionHandler;
    private MediatorLiveData<Integer> countLike, countComment, countShare;
    private MediatorLiveData<Boolean> isLiked;
    private MediatorLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;

    public PostDataViewModel(SessionHandler.SessionRegistry sessionRegistry, Post data) {
        super(sessionRegistry, data);
        countLike = new MediatorLiveData<>();
        countLike.setValue(data.getLikeCount());
        countComment = new MediatorLiveData<>();
        countComment.setValue(data.getCommentCount());
        countShare = new MediatorLiveData<>();
        countShare.setValue(data.getShareCount());
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

        PostSessionHandler sessionHandler = new PostSessionHandler(data.getId());
        postSessionHandler = sessionRegistry.register(sessionHandler);

        countLikeContent.addSource(postSessionHandler, new Observer<PostSessionHandler>() {
            @Override
            public void onChanged(PostSessionHandler s) {
                MutableLiveData<PostSessionHandler.PostDataSync> dataSync = s.getDataSyncEmitter();

                countLike.addSource(dataSync, new Observer<PostSessionHandler.PostDataSync>() {
                    @Override
                    public void onChanged(PostSessionHandler.PostDataSync postDataSync) {
                        countLike.setValue(postDataSync.likeCount);
                    }
                });
                countComment.addSource(dataSync, new Observer<PostSessionHandler.PostDataSync>() {
                    @Override
                    public void onChanged(PostSessionHandler.PostDataSync postDataSync) {
                        countLike.setValue(postDataSync.commentCount);
                    }
                });
                countShare.addSource(dataSync, new Observer<PostSessionHandler.PostDataSync>() {
                    @Override
                    public void onChanged(PostSessionHandler.PostDataSync postDataSync) {
                        countLike.setValue(postDataSync.shareCount);
                    }
                });
                countLikeContent.removeSource(postSessionHandler);
            }
        });

    }

    public MutableLiveData<PostSessionHandler> getPostSessionHandler() {
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

    public MutableLiveData<Integer> getCountShare() {
        return countShare;
    }

    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }

}
