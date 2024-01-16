package com.example.socialmediaapp.models;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.socialmediaapp.models.user.UserInformation;

public class UserSession {
  private UserInformation userInfo;
  private String avatarUri;
  private String bgUri;
  private MediatorLiveData<Bitmap> avatar;
  private MediatorLiveData<Bitmap> background;

  public UserSession() {
  }

  public UserInformation getUserInfo() {
    return userInfo;
  }

  public void setUserInfo(UserInformation userInfo) {
    this.userInfo = userInfo;
  }

  public MediatorLiveData<Bitmap> getAvatar() {
    return avatar;
  }

  public void setAvatar(MediatorLiveData<Bitmap> avatar) {
    this.avatar = avatar;
  }

  public MediatorLiveData<Bitmap> getBackground() {
    return background;
  }

  public void setBackground(MediatorLiveData<Bitmap> background) {
    this.background = background;
  }

  public String getAvatarUri() {
    return avatarUri;
  }

  public void setAvatarUri(String avatarUri) {
    this.avatarUri = avatarUri;
  }

  public String getBgUri() {
    return bgUri;
  }

  public void setBgUri(String bgUri) {
    this.bgUri = bgUri;
  }
}
