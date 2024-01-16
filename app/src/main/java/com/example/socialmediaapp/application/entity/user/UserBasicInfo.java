package com.example.socialmediaapp.application.entity.user;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserBasicInfo {
    @PrimaryKey(autoGenerate = true)
    private Integer autoId;
    public Integer getAutoId() {
        return autoId;
    }
    public void setAutoId(Integer autoId) {
        this.autoId = autoId;
    }
    private String id;
    private String fullname;
    private String alias;
    private String avatarId;
    public UserBasicInfo() {
    }

    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }


}
