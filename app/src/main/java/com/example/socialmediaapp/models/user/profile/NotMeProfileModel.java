package com.example.socialmediaapp.models.user.profile;

import com.example.socialmediaapp.models.user.profile.base.ProfileModel;

public class NotMeProfileModel extends ProfileModel {

    private String type;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public NotMeProfileModel() {
        super();
    }
}
