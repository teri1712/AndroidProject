package com.example.socialmediaapp.viewmodels.models.post.base;

import android.content.Context;

import com.example.socialmediaapp.apis.entities.PostBody;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;

public class Post {
    private UserBasicInfo author;
    private String status;
    private Integer id;
    private String type;
    private String time;
    private Integer likeCount, commentCount, shareCount;
    private boolean liked;


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Post() {
    }

    public boolean isLiked() {
        return liked;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }


    public UserBasicInfo getAuthor() {
        return author;
    }

    public String getType() {
        return type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAuthor(UserBasicInfo author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
