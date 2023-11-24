package com.example.socialmediaapp.viewmodels.items;

import android.content.Context;
import android.net.Uri;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodels.models.post.Comment;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PostItemViewModel extends ViewModel {
    private Post post;
    private MutableLiveData<Integer> countLike, countComment, countShare;
    private MutableLiveData<Boolean> isLiked;
    private MutableLiveData<String> time;
    private MediatorLiveData<String> countLikeContent;
    private ItemRepository<Comment> commentRepo;

    public PostItemViewModel(Post post) {
        this.post = post;
        commentRepo = new ItemRepository<>();
        countLike = new MutableLiveData<>(post.getLikeCount());
        countComment = new MutableLiveData<>(post.getCommentCount());
        countShare = new MutableLiveData<>(post.getShareCount());
        isLiked = new MutableLiveData<>(post.isLiked());
        time = new MutableLiveData<>(post.getTime());

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

    public ItemRepository<Comment> getCommentRepo() {
        return commentRepo;
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
                countLikeContent.setValue(post.getAuthor().getFullname());
            }
        }
    }

    public Post getPost() {
        return post;
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

    public MutableLiveData<String> loadComments(Context context) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        MutableLiveData<List<Comment>> listener = new MutableLiveData<>();
        commentRepo.getUpdateOnRepo().addSource(listener, new Observer<List<Comment>>() {
            @Override
            public void onChanged(List<Comment> comments) {
                if (comments.isEmpty()) {
                    callBack.setValue("Nothing loaded");
                    return;
                }
                callBack.setValue(Integer.toString(comments.size()) + " items loaded");
                for (Comment comment : comments) {
                    commentRepo.addToEnd(comment);
                }
                commentRepo.getUpdateOnRepo().removeSource(listener);
            }
        });
        ServiceApi.loadComments(context, post.getId(), listener);
        return callBack;
    }

    public MutableLiveData<String> sendComment(Context context, String commentContent, Uri image) {
        MutableLiveData<String> callBack = new MutableLiveData<>();
        MutableLiveData<Comment> listener = new MutableLiveData<>();
        commentRepo.getUpdateOnRepo().addSource(listener, new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                if (comment != null) {
                    commentRepo.addToBegin(comment);
                }
                commentRepo.getUpdateOnRepo().removeSource(listener);
                callBack.setValue(comment == null ? "Failed" : "Success");
            }
        });
        ServiceApi.sendComment(context, post.getId(), commentContent, image, listener);
        return callBack;
    }

}
