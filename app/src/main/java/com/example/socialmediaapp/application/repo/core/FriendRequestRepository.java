package com.example.socialmediaapp.application.repo.core;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.application.session.FriendRequestAccessHandler;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.ProfileSessionHandler;
import com.example.socialmediaapp.models.user.FriendRequestModel;

import java.util.HashMap;

public class FriendRequestRepository extends RealTimeRepository<FriendRequestModel> {
  private OnlineSessionHandler.Vcc profileProvider;
  private FriendRequestAccessHandler friendRequestAccessHandler;

  public FriendRequestRepository(FriendRequestAccessHandler friendRequestAccessHandler) {
    super(friendRequestAccessHandler);
    this.friendRequestAccessHandler = friendRequestAccessHandler;
    this.profileProvider = DecadeApplication.getInstance().onlineSessionHandler.getProfileProvider();
    assert profileProvider != null;
  }

  public LiveData<String> accept(String userAlias) {
    LiveData<ProfileSessionHandler> profileHandler = profileProvider.getUserProfile(userAlias);
    MediatorLiveData<String> callBack = new MediatorLiveData<>();
    Task task = new Task(callBack);
    task.action = () -> doAccept(userAlias, callBack, profileHandler);
    submit(task);
    return task.ml;
  }

  private void doAccept(String alias,
                        MediatorLiveData<String> callBack,
                        LiveData<ProfileSessionHandler> profileHandler) {
    callBack.addSource(profileHandler, sessionHandler -> {
      callBack.removeSource(profileHandler);
      callBack.addSource(sessionHandler
              .acceptFriendRequest(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          if (s.equals("Success")) {
            delete(alias);
          }
          callBack.setValue(s);
        }
      });
    });
  }

  public LiveData<String> reject(String userAlias) {
    LiveData<ProfileSessionHandler> profileHandler = profileProvider.getUserProfile(userAlias);
    MediatorLiveData<String> callBack = new MediatorLiveData<>();
    Task task = new Task(callBack);
    task.action = () -> doReject(userAlias, callBack, profileHandler);
    submit(task);
    return task.ml;
  }


  private void doReject(String alias,
                        MediatorLiveData<String> callBack,
                        LiveData<ProfileSessionHandler> profileHandler) {
    callBack.addSource(profileHandler, sessionHandler -> {
      callBack.addSource(sessionHandler
              .rejectFriendRequest(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          if (s.equals("Success")) {
            delete(alias);
          }
          callBack.setValue(s);
        }
      });
    });
  }

  private void delete(String userId) {
    int pos;
    for (pos = 0; pos < items.size(); pos++) {
      if (items.get(pos).getUserModel().getId().equals(userId)) {
        break;
      }
    }
    countLoaded--;
    items.remove(pos);
    HashMap<String, Object> data = new HashMap<>();
    data.put("offset", pos);
    setUpdate(new Update(Update.Op.REMOVE, data));
    friendRequestAccessHandler.delete(userId);
  }

}
