package com.example.socialmediaapp.viewmodels.models;

import android.content.Context;

import com.example.socialmediaapp.apis.entities.HomeEntranceBody;

public class HomePageContent {
    private UserSession user;

    public UserSession getUser() {
        return user;
    }

    public void setUser(UserSession user) {
        this.user = user;
    }
    public HomePageContent(HomeEntranceBody homeEntranceBody, Context context) {
        user = new UserSession(homeEntranceBody.getUser(), context);
    }
}
