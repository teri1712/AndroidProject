package com.example.socialmediaapp.viewmodel.items;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;

public class ReplyCommentItemViewModel extends ViewModel {
    private ReplyComment replyComment;
    private MediatorLiveData<Integer> countLike;
    private MutableLiveData<Boolean> isLiked;
    private MutableLiveData<String> time;
    public ReplyCommentItemViewModel(ReplyComment replyComment) {
        this.replyComment = replyComment;
        countLike = new MediatorLiveData<>();
        countLike.setValue(replyComment.getCountLike() + (replyComment.isLiked() ? -1 : 1));
        isLiked = new MutableLiveData<>(replyComment.isLiked());
        time = new MutableLiveData<>(replyComment.getTime());
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

    public ReplyComment getReplyComment() {
        return replyComment;
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
