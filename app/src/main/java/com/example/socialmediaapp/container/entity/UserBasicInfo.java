package com.example.socialmediaapp.container.entity;


import android.content.Context;
import android.net.Uri;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;

@Entity
public class UserBasicInfo {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String fullname;
    private String alias;
    private String avatarUri;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public UserBasicInfo() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
