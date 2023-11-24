package com.example.socialmediaapp.apis.entities;

import java.sql.Date;

public class UserProfileBody {
    private String fullname;
    private String alias;
    private String gender;
    private String birthday;
    private PostBody avatarPost;
    private PostBody backgroundPost;
    private String type;

    public UserProfileBody() {
    }

    public PostBody getAvatarPost() {
        return avatarPost;
    }

    public void setAvatarPost(PostBody avatarPost) {
        this.avatarPost = avatarPost;
    }

    public PostBody getBackgroundPost() {
        return backgroundPost;
    }

    public void setBackgroundPost(PostBody backgroundPost) {
        this.backgroundPost = backgroundPost;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

}
