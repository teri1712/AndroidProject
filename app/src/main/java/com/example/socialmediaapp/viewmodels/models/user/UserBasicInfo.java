package com.example.socialmediaapp.viewmodels.models.user;

import android.content.Context;
import android.graphics.drawable.Drawable;


import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.services.ServiceApi;

public class UserBasicInfo {
    private String fullname;
    private String alias;
    private Drawable avatar;

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

    public Drawable getAvatar() {
        return avatar;
    }

    public void setAvatar(Drawable avatar) {
        this.avatar = avatar;
    }

}
