package com.example.socialmediaapp.api.entities;

import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

public class MessageItemBody {
    private ChatInfo chatInfo;
    private String sender;
    private String id;
    private Long time;
    private String type;
    private String content;
    private Integer ord;
    private ImageBody imageBody;

    public MessageItemBody() {

    }

    public ChatInfo getChatInfo() {
        return chatInfo;
    }

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }


    public void setId(String id) {
        this.id = id;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }

    public ImageBody getImageBody() {
        return imageBody;
    }

    public void setImageBody(ImageBody imageBody) {
        this.imageBody = imageBody;
    }
}

