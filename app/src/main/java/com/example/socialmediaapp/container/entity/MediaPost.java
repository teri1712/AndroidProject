package com.example.socialmediaapp.container.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Post.class,
        parentColumns = "id",
        childColumns = "postId",
        onDelete = ForeignKey.SET_NULL
))
public class MediaPost {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer postId;
    private Integer mediaId;

    public MediaPost() {
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

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }
}
