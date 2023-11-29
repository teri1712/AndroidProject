package com.example.socialmediaapp.layoutviews.profile.model;

import android.content.Context;
import android.widget.Toast;

import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;

public abstract class Configurer {

    private boolean actionLeftAllowed, actionRightAllowed;
    private Context context;
    protected ViewProfileSessionHandler handler;

    public Configurer(Context context, ViewProfileSessionHandler handler) {
        this.context = context;
        this.handler = handler;
        actionLeftAllowed = false;
        actionRightAllowed = false;
    }

    public abstract void configure();

    protected abstract void leftAction();

    protected abstract void rightAction();

    public void allowActionLeft() {
        actionLeftAllowed = true;
    }

    public void allowActionRight() {
        actionRightAllowed = true;
    }

    public void performActionLeft() {
        if (!actionLeftAllowed) {
            Toast.makeText(context, "You can't do this action right now", Toast.LENGTH_SHORT).show();
            return;
        }
        leftAction();
    }

    public void performActionRight() {
        if (!actionRightAllowed) {
            Toast.makeText(context, "You can't do this action right now", Toast.LENGTH_SHORT).show();
            return;
        }
        rightAction();
    }

}
