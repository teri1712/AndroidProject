package com.example.socialmediaapp.viewmodel.items;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.container.session.CommentSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;
import com.example.socialmediaapp.viewmodel.models.repo.ItemRepository;

public class CommentItemViewModel extends ViewModel {
    private Comment comment;
    private MediatorLiveData<Integer> countLike;
    private MutableLiveData<Boolean> isLiked;
    private MutableLiveData<String> time;
    private CommentSessionHandler commentSessionHandler;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;

    public CommentItemViewModel() {
        this.commentSessionHandler = commentSessionHandler;

        sessionRegistry = commentSessionHandler.getSessionRegistry();
        sessionState = commentSessionHandler.getSessionState();

        countLike = new MediatorLiveData<>();
        countLike.setValue(comment.getCountLike() + (comment.isLiked() ? -1 : 1));
        isLiked = new MutableLiveData<>(comment.isLiked());
        time = new MutableLiveData<>(comment.getTime());
        countLike.addSource(isLiked, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    countLike.setValue(countLike.getValue() - 1);
                } else {
                    countLike.setValue(countLike.getValue() + 1);
                }
            }
        });
    }


    public MutableLiveData<String> getTime() {
        return time;
    }

    public MutableLiveData<Integer> getCountLike() {
        return countLike;
    }

    public MutableLiveData<Boolean> getIsLiked() {
        return isLiked;
    }
}
