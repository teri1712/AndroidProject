package com.example.socialmediaapp.viewmodel.models.messenger;

public class TextMessageItem extends MessageItem {
    private String text;
    public TextMessageItem(){

    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
