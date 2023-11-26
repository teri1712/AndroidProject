package com.example.socialmediaapp.apis.entities;

import java.sql.Date;
import java.util.List;

public class UserSessionBody {
    private UserInformationBody userInfo;
    private Integer avatarId;
    private Integer backgroundId;

    public Integer getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(Integer avatarId) {
        this.avatarId = avatarId;
    }

    public Integer getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(Integer backgroundId) {
        this.backgroundId = backgroundId;
    }

    public UserInformationBody getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInformationBody userInfo) {
        this.userInfo = userInfo;
    }
}
