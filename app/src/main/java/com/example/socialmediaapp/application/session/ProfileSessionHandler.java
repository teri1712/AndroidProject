package com.example.socialmediaapp.application.session;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.user.ProfileDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.user.UserProfile;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.models.user.profile.base.ProfileModel;

import java.util.Map;

public class ProfileSessionHandler extends SessionHandler {
  protected volatile UserProfile profile;
  protected DataAccessHandler<HandlerAccess> postDataAccess;
  protected MutableLiveData<HandlerAccess> avtPostAccess;
  protected MutableLiveData<HandlerAccess> bgPostAccess;
  protected MutableLiveData<ProfileModel> profileLivedata;
  private final UserProfileProvider profileProvider;

  public ProfileSessionHandler(
          UserProfileProvider profileProvider,
          UserProfile profile) {
    this.profileProvider = profileProvider;
    this.profile = profile;
    profileLivedata = new MutableLiveData<>(ModelConvertor.convertToUserProfileModel(profile));
    avtPostAccess = new MutableLiveData<>();
    bgPostAccess = new MutableLiveData<>();

    PostHandlerStore postSessionStore = PostHandlerStore.getInstance();
    String avatarPostId = profile.getAvatarPostId();
    String bgPostId = profile.getBackgroundPostId();
    Integer avatarAccessId = profile.getAvatarPostAccessId();
    Integer bgAccessId = profile.getBackgroundPostAccessId();
    if (avatarAccessId != null) {
      avtPostAccess = new MutableLiveData<>(postSessionStore
              .getHandlerAccess(avatarPostId, avatarAccessId)
      );
    }
    if (bgAccessId != null) {
      avtPostAccess = new MutableLiveData<>(postSessionStore
              .getHandlerAccess(bgPostId, bgAccessId));
    }
    init();
  }

  public DataAccessHandler<HandlerAccess> getPostDataAccess() {
    return postDataAccess;
  }

  public LiveData<ProfileModel> getProfileLivedata() {
    return profileLivedata;
  }

  @Override
  protected void init() {
    super.init();
    UserPostAccessHelper helper = new UserPostAccessHelper(profile.getId());
    postDataAccess = new DataAccessHandler<>(helper);
  }

  @Override
  protected void invalidate() {
    PostHandlerStore postSessionStore = PostHandlerStore.getInstance();
    String avatarPostId = profile.getAvatarPostId();
    String bgPostId = profile.getBackgroundPostId();

    Integer avatarAccessId = profile.getAvatarPostAccessId();
    Integer bgAccessId = profile.getBackgroundPostAccessId();

    HandlerAccess avatarAccess = avatarAccessId == null
            ? null
            : postSessionStore
            .getHandlerAccess(avatarPostId, avatarAccessId);
    HandlerAccess bgAccess = bgAccessId == null
            ? null
            : postSessionStore
            .getHandlerAccess(bgPostId, bgAccessId);

    if (avatarAccess != null) {
      avatarAccess.release();
    }
    if (bgAccess != null) {
      bgAccess.release();
    }
    super.invalidate();
  }

  public LiveData<HandlerAccess> getAvtPostAccess() {
    return avtPostAccess;
  }

  public LiveData<HandlerAccess> getBgPostAccess() {
    return bgPostAccess;
  }

  public LiveData<String> accept() {
    return profileProvider.acceptRequest(profile.getId());
  }

  public LiveData<String> reject() {
    return profileProvider.rejectRequest(profile.getId());
  }

  public LiveData<String> send() {
    return profileProvider.sendRequest(profile.getId());
  }

  public LiveData<String> cancel() {
    return profileProvider.cancelRequest(profile.getId());
  }
}
