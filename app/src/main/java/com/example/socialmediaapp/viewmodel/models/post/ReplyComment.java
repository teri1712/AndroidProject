package com.example.socialmediaapp.viewmodel.models.post;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.socialmediaapp.apis.entities.ReplyCommentBody;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class ReplyComment {
    private UserBasicInfo sender;
    private Integer id;
    private String content;
    private Drawable image;
    private boolean liked;
    private String time;
    private Integer countLike;

    public boolean isLiked() {
        return liked;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
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

    public ReplyComment(ReplyCommentBody replyCommentBody, Context context) {
        sender = new UserBasicInfo(replyCommentBody.getSender(), context);
        id = replyCommentBody.getId();
        content = replyCommentBody.getContent();
        image = ServiceApi.loadImage(context, replyCommentBody.getMediaId());
        liked = replyCommentBody.isLiked();
        time = replyCommentBody.getTime();
        countLike = replyCommentBody.getCountLike();
    }

    public UserBasicInfo getSender() {
        return sender;
    }

    public void setSender(UserBasicInfo sender) {
        this.sender = sender;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
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


    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        Comment c = (Comment) obj;
        return id == c.getId();
    }


}
