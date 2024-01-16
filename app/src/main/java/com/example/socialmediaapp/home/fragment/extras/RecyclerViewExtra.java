package com.example.socialmediaapp.home.fragment.extras;

import android.view.View;

public abstract class RecyclerViewExtra {
    public enum Position {
        START, END
    }

    private boolean isConfigured;
    protected View view;
    protected Position pos;

    public RecyclerViewExtra(View view, Position pos) {
        this.view = view;
        this.pos = pos;
        isConfigured = false;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public void setConfigured(boolean configured) {
        isConfigured = configured;
    }

    public Position getPos() {
        return pos;
    }

    public View getView() {
        return view;
    }

    public abstract void configure(View view);

}

