package com.example.socialmediaapp.container.entity;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class UserInformation {
    private String fullname;
    private String alias;
    private String gender;
    private String birthday;
    private String avatarUri, backgroundUri;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UserInformation() {
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getBackgroundUri() {
        return backgroundUri;
    }

    public void setBackgroundUri(String backgroundUri) {
        this.backgroundUri = backgroundUri;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


}
