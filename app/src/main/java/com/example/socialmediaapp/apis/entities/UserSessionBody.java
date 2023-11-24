package com.example.socialmediaapp.apis.entities;

import java.sql.Date;
import java.util.List;

public class UserSessionBody {
    private UserInformationBody userInfo;
    private List<UserBasicInfoBody> recentSearch;
    private PostBody avatarPost;
    private PostBody backgroundPost;

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

    public UserInformationBody getUserInfo() {
        return userInfo;
    }

    public List<UserBasicInfoBody> getRecentSearch() {
        return recentSearch;
    }

    public void setRecentSearch(List<UserBasicInfoBody> recentSearch) {
        this.recentSearch = recentSearch;
    }

    public void setUserInfo(UserInformationBody userInfo) {
        this.userInfo = userInfo;
    }
}
