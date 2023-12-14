package com.example.socialmediaapp.apis.entities;

import androidx.room.PrimaryKey;

public class MessageItemBody {
    private String sender;
    private Long time;
    private String type;
    private String content;
    private Integer ord;
    private Integer mediaId;

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getMediaId() {
        return mediaId;
    }

    public void setMediaId(Integer mediaId) {
        this.mediaId = mediaId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
