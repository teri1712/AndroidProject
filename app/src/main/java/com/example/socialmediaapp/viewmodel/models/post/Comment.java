package com.example.socialmediaapp.viewmodel.models.post;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class Comment {
    private UserBasicInfo author;
    private Integer id;
    private String content;
    private Bitmap image;
    private boolean liked;
    private String time;
    private Integer countLike, countComment;

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public Integer getCountComment() {
        return countComment;
    }

    public void setCountComment(Integer countComment) {
        this.countComment = countComment;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getCountLike() {
        return countLike;
    }

    public void setCountLike(Integer countLike) {
        this.countLike = countLike;
    }

    public Comment() {
    }

    public UserBasicInfo getAuthor() {
        return author;
    }

    public void setAuthor(UserBasicInfo author) {
        this.author = author;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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


}
