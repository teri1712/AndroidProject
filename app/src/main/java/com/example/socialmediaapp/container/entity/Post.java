package com.example.socialmediaapp.container.entity;

import android.content.Context;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.apis.entities.PostBody;

@Entity(foreignKeys = @ForeignKey(
        entity = UserBasicInfo.class,
        parentColumns = "id",
        childColumns = "authorId",
        onDelete = ForeignKey.SET_NULL
))
public class Post {
    @PrimaryKey
    private Integer id;
    private String status;
    private String type;
    private String time;
    private Integer authorId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}
