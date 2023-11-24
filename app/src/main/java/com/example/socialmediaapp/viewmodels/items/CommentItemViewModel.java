package com.example.socialmediaapp.viewmodels.items;

import android.content.Context;
import android.net.Uri;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.Comment;
import com.example.socialmediaapp.viewmodels.models.post.ReplyComment;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;
import com.example.socialmediaapp.viewmodels.models.repo.ItemRepository;

import java.util.List;

public class CommentItemViewModel extends ViewModel {
    private Comment comment;
    private MediatorLiveData<Integer> countLike;
    private MutableLiveData<Boolean> isLiked;
    private MutableLiveData<String> time;
    private ItemRepository<ReplyComment> replyCommentRepo;

    public CommentItemViewModel(Comment comment) {
        this.comment = comment;
        replyCommentRepo = new ItemRepository<>();
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
    public Comment getComment() {
        return comment;
    }
    public ItemRepository<ReplyComment> getReplyCommentRepo() {
        return replyCommentRepo;
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
