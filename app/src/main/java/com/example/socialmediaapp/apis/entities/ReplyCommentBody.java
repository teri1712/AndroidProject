package com.example.socialmediaapp.apis.entities;

public class ReplyCommentBody {
    private UserBasicInfoBody sender;
    private Integer id;
    private String content;
    private Integer mediaId;
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

    public ReplyCommentBody(){}

    public Integer getId() {
        return id;
    }

    public UserBasicInfoBody getSender() {
        return sender;
    }

    public void setSender(UserBasicInfoBody sender) {
        this.sender = sender;
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

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

}
