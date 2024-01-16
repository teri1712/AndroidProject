package com.example.socialmediaapp.api.entities;

public class UserSessionBody {
    private UserInformationBody userInfo;
    private String avatarId;
    private String backgroundId;


    public String getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(String avatarId) {
        this.avatarId = avatarId;
    }

    public String getBackgroundId() {
        return backgroundId;
    }

    public void setBackgroundId(String backgroundId) {
        this.backgroundId = backgroundId;
    }

    public UserInformationBody getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInformationBody userInfo) {
        this.userInfo = userInfo;
    }
}
