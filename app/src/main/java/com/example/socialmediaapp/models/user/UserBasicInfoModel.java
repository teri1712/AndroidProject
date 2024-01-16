package com.example.socialmediaapp.models.user;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

public class UserBasicInfoModel {
  private String fullname;
  private String id;
  private String alias;
  private String avatarUri;

  /*Reasonable*/
  private Bitmap scaled;

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

  public UserBasicInfoModel() {
  }

  public String getFullname() {
    return fullname;
  }

  public String getAvatarUri() {
    return avatarUri;
  }

  public void setAvatarUri(String avatarUri) {
    this.avatarUri = avatarUri;
  }

  public Bitmap getScaled() {
    return scaled;
  }

  public void setScaled(Bitmap scaled) {
    this.scaled = scaled;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }


}
