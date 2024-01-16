package com.example.socialmediaapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.session.ProfileSessionHandler;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.user.profile.base.ProfileModel;
import com.example.socialmediaapp.viewmodel.item.DataViewModel;

public class ProfileViewModel extends DataViewModel<ProfileModel> {
  private ProfileSessionHandler handler;
  private ChatInfo chatInfo;
  private LiveData<HandlerAccess> avatarAccess, bgAccess;

  public ProfileViewModel(ProfileSessionHandler handler) {
    super();
    this.handler = handler;
    liveData = (MutableLiveData<ProfileModel>) this.handler.getProfileLivedata();
    chatInfo = handler.getProfileLivedata().getValue().getChatInfo();
    avatarAccess = handler.getAvtPostAccess();
    bgAccess = handler.getBgPostAccess();
  }


  public ProfileSessionHandler getHandler() {
    return handler;
  }

  public ChatInfo getChatInfo() {
    return chatInfo;
  }

  public LiveData<HandlerAccess> getAvatarAccess() {
    return avatarAccess;
  }

  public LiveData<HandlerAccess> getBgAccess() {
    return bgAccess;
  }

}
