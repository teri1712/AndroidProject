package com.example.socialmediaapp.application.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = UserBasicInfo.class,
        parentColumns = "id",
        childColumns = "authorId",
        onDelete = ForeignKey.SET_NULL
))
public class Comment {
    @PrimaryKey
    private Integer id;
    private String content;
    private String time;
    private Integer authorId;
    private Integer likeCount;
    private boolean liked;
    private Integer ord;

    private Integer sessionId;
    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Comment() {
    }

    public boolean isLiked() {
        return liked;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}
