package com.example.socialmediaapp.viewmodel.models.user.profile;

import android.content.Context;

import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

public class NotMeProfile extends UserProfile {

    private String type;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public NotMeProfile() {
        super();
    }
}
