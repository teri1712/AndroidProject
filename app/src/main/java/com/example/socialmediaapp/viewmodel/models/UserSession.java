package com.example.socialmediaapp.viewmodel.models;

import android.graphics.Bitmap;

import com.example.socialmediaapp.viewmodel.models.user.UserInformation;

public class UserSession {
    private UserInformation userInfo;
    private Bitmap avatar;
    private Bitmap background;

    public UserInformation getUserInfo() {
        return userInfo;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public Bitmap getBackground() {
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public void setUserInfo(UserInformation userInfo) {
        this.userInfo = userInfo;
    }

    public UserSession() {
    }
}
