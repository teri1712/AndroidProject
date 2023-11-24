package com.example.socialmediaapp.viewmodels.models;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.example.socialmediaapp.apis.entities.UserBasicInfoBody;
import com.example.socialmediaapp.apis.entities.UserInformationBody;
import com.example.socialmediaapp.apis.entities.UserSessionBody;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodels.models.user.UserInformation;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class UserSession {
    private UserInformation userInfo;
    private ImagePost avatarPost;
    private ImagePost backgroundPost;

    private List<UserBasicInfo> recentSearch;

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

    public List<UserBasicInfo> getRecentSearch() {
        return recentSearch;
    }

    public void setRecentSearch(List<UserBasicInfo> recentSearch) {
        this.recentSearch = recentSearch;
    }

    public UserInformation getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInformation userInfo) {
        this.userInfo = userInfo;
    }

    public UserSession(UserSessionBody userSessionBody, Context context) {
        userInfo = new UserInformation();
        UserInformationBody userInformationBody = userSessionBody.getUserInfo();
        userInfo.setAlias(userInformationBody.getAlias());
        userInfo.setFullname(userInformationBody.getFullname());
        userInfo.setBirthday(userInformationBody.getBirthday());
        userInfo.setGender(userInformationBody.getGender());
        if (userSessionBody.getAvatarPost() != null) {
            avatarPost = new ImagePost(userSessionBody.getAvatarPost(), context);
        }
        if (userSessionBody.getBackgroundPost() != null) {
            backgroundPost = new ImagePost(userSessionBody.getBackgroundPost(), context);
        }
        recentSearch = new ArrayList<>();
        for (UserBasicInfoBody u : userSessionBody.getRecentSearch()) {
            recentSearch.add(new UserBasicInfo(u, context));
        }
    }
}
