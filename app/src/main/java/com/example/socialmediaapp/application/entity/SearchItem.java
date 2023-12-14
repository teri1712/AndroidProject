package com.example.socialmediaapp.application.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.Query;

import retrofit2.http.Path;

@Entity(foreignKeys = @ForeignKey(
        entity = UserBasicInfo.class,
        parentColumns = "autoId",
        childColumns = "userInfoId",
        onDelete = ForeignKey.CASCADE
))
public class SearchItem {
    @PrimaryKey(autoGenerate = true)
    private Integer autoId;

    private Integer userInfoId;
    private Integer ord;


    public Integer getAutoId() {
        return autoId;
    }

    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }

    public Integer getUserInfoId() {
        return userInfoId;
    }

    public void setUserInfoId(Integer userInfoId) {
        this.userInfoId = userInfoId;
    }

    public Integer getOrd() {
        return ord;
    }

    public void setOrd(Integer ord) {
        this.ord = ord;
    }
}
