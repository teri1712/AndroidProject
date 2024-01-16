package com.example.socialmediaapp.api.entities;


public class FriendRequestBody {
  private Long time;
  private UserBasicInfoBody userBody;
  private String id;

  public FriendRequestBody() {
  }

  public Long getTime() {
    return time;
  }

  public void setTime(Long time) {
    this.time = time;
  }

  public UserBasicInfoBody getUserBody() {
    return userBody;
  }

  public void setUserBody(UserBasicInfoBody userBody) {
    this.userBody = userBody;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
