package com.example.socialmediaapp.viewmodel.models.user.profile.base;

import android.content.Context;

import com.example.socialmediaapp.apis.entities.UserProfileBody;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;

public class UserProfile {

    protected String fullname;

    protected String alias;
    protected ImagePost avatarPost;
    protected ImagePost backgroundPost;

    protected String gender;

    protected String birthday;

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UserProfile() {
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public ImagePost getAvatarPost() {
        return avatarPost;
    }

    public void setAvatarPost(ImagePost avatarPost) {
        this.avatarPost = avatarPost;
    }

    public ImagePost getBackgroundPost() {
        return backgroundPost;
    }

    public void setBackgroundPost(ImagePost backgroundPost) {
        this.backgroundPost = backgroundPost;
    }
}
