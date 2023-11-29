package com.example.socialmediaapp.application.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Post.class,
        parentColumns = "id",
        childColumns = "postId",
        onDelete = ForeignKey.CASCADE
))
public class ImagePost {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer postId;
    private String imageUri;

    private Integer sessionId;

    public Integer getSessionId() {
        return sessionId;
    }

    public void setSessionId(Integer sessionId) {
        this.sessionId = sessionId;
    }

    public ImagePost() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPostId() {
        return postId;
    }

    public void setPostId(Integer postId) {
        this.postId = postId;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
