package com.example.socialmediaapp.application.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Post.class,
        parentColumns = "autoId",
        childColumns = "postId",
        onDelete = ForeignKey.CASCADE
))
public class ImagePost {
    @PrimaryKey(autoGenerate = true)
    private Integer autoId;
    private Integer postId;

    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    private String imageUri;

    public ImagePost() {
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
