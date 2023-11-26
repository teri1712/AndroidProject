package com.example.socialmediaapp.viewmodel.models.user;

import android.graphics.Bitmap;

public class UserBasicInfo {
    private String fullname;
    private String alias;
    private Bitmap avatar;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UserBasicInfo() {
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

}
