package com.example.socialmediaapp.models.messenger.message;

import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

public class TextMessageItemModel extends MessageItemModel {
    private String text;
    public TextMessageItemModel(){

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
