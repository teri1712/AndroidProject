package com.example.socialmediaapp.application.session;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.api.FriendApi;
import com.example.socialmediaapp.api.UserApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.PostBody;
import com.example.socialmediaapp.api.entities.UserProfileBody;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.dao.user.FriendDao;
import com.example.socialmediaapp.application.dao.user.ProfileDao;
import com.example.socialmediaapp.application.dao.user.UserBasicInfoDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.post.Post;
import com.example.socialmediaapp.application.entity.user.FriendRequestItem;
import com.example.socialmediaapp.application.entity.user.UserProfile;
import com.example.socialmediaapp.application.network.SerialTaskRequest;
import com.example.socialmediaapp.application.network.SingleTaskRequest;
import com.example.socialmediaapp.application.network.Task;
import com.example.socialmediaapp.application.network.TaskRequest;
import com.example.socialmediaapp.view.container.ShadowRoundedClickablePanel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/* will implement caching later */
public class UserProfileProvider extends SessionHandler {
  private final TreeMap<String, List<MutableLiveData<ProfileSessionHandler>>> fetchWaits;
  private final TreeMap<String, ProfileSessionHandler> profiles;
  private final ProfileDao profileDao;
  private final Handler mainLooper;
  private SelfProfileSessionHandler selfProfile;
  private final UserPrincipal principal;
  private final UserSessionHandler userHandler;
  private final FriendRequestAccessHandler fReqAccess;

  public UserProfileProvider(
          UserPrincipal principal,
          UserSessionHandler userHandler) {
    this.userHandler = userHandler;
    this.principal = principal;
    this.fReqAccess = userHandler.fReqAccess;
    profiles = new TreeMap<>();
    fetchWaits = new TreeMap<>();
    profileDao = DecadeDatabase.getInstance()
            .getProfileDao();
    mainLooper = new Handler(Looper.getMainLooper());
  }

  protected TaskRequest createSingleTask(Runnable action) {
    SingleTaskRequest.Builder builder = new SingleTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(action);
    TaskRequest request = builder
            .setWillRestore(false)
            .build();
    return request;
  }

  protected TaskRequest createSerialTask(Runnable action) {
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ActionHandleTask task = builder.fromTask(ActionHandleTask.class);
    task.setHandler(this);
    task.setAction(action);
    TaskRequest request = builder
            .setAlias("User profile resolver")
            .setWillRestore(false)
            .build();
    return request;
  }

  private void onProfileAvailable(UserProfile userProfile, String userId) {
    String self = principal.getUserId();
    ProfileSessionHandler profileHandler;
    if (self.equals(userId)) {
      selfProfile = new SelfProfileSessionHandler(this, userProfile);
      profileHandler = selfProfile;
      mainLooper.post(() -> userHandler.profileBinder.bind(selfProfile));
    } else {
      profileHandler = new ProfileSessionHandler(this, userProfile);
    }
    profiles.put(userId, profileHandler);
    List<MutableLiveData<ProfileSessionHandler>> waits = fetchWaits.remove(userId);
    for (MutableLiveData<ProfileSessionHandler> wait : waits) {
      wait.postValue(profileHandler);
    }
  }

  private void downloadProfile(String userId) {
    try {
      Response<UserProfileBody> res = HttpCallSupporter.create(UserApi.class)
              .loadUserProfile(userId).execute();
      HttpCallSupporter.debug(res);
      UserProfileBody body = res.body();
      UserProfile userProfile = DtoConverter.convertToUserProfile(body);
      PostBody avatarPost = body.getAvatarPost();
      PostBody bgPost = body.getBackgroundPost();
      if (avatarPost != null) {
        Map<String, Object> itemPack = DtoConverter.convertToPost(avatarPost);
        Post item = (Post) itemPack.get("post");
        HandlerAccess access = PostHandlerStore
                .getInstance()
                .register(item, itemPack);
        userProfile.setAvatarPostId(item.getId());
        userProfile.setAvatarPostAccessId(access.getId());
      }
      if (bgPost != null) {
        Map<String, Object> itemPack = DtoConverter.convertToPost(bgPost);
        Post item = (Post) itemPack.get("post");
        HandlerAccess access = PostHandlerStore
                .getInstance()
                .register(item, itemPack);
        userProfile.setAvatarPostId(item.getId());
        userProfile.setBackgroundPostAccessId(access.getId());
      }
      profileDao.insert(userProfile);

      post(() -> onProfileAvailable(userProfile, userId));
    } catch (IOException e) {
      assert false;
      e.printStackTrace();
    }
  }

  public LiveData<SelfProfileSessionHandler> getSelfProfile() {
    MediatorLiveData<SelfProfileSessionHandler> callBack = new MediatorLiveData<>();
    LiveData<ProfileSessionHandler> res = getUserProfile(principal.getUserId());
    callBack.addSource(res, new Observer<ProfileSessionHandler>() {
      @Override
      public void onChanged(ProfileSessionHandler profileSessionHandler) {
        callBack.setValue((SelfProfileSessionHandler) profileSessionHandler);
      }
    });
    return callBack;
  }

  public LiveData<ProfileSessionHandler> getUserProfile(final String userId) {
    MutableLiveData<ProfileSessionHandler> callBack = new MutableLiveData<>();
    post(() -> {
      ProfileSessionHandler profileHandler = profiles.get(userId);
      if (profileHandler == null) {
        List<MutableLiveData<ProfileSessionHandler>> waits = fetchWaits.get(userId);
        if (waits == null) {
          waits = new ArrayList<>();
          fetchWaits.put(userId, waits);

          UserProfile profile = profileDao.findById(userId);
          if (profile != null) {
            onProfileAvailable(profile, userId);
            return;
          }
          postTask(createSingleTask(() -> downloadProfile(userId)));
        }
        waits.add(callBack);
        return;
      }
      callBack.postValue(profileHandler);
    });
    return callBack;
  }

  @Override
  protected void invalidate() {
    for (ProfileSessionHandler profile : profiles.values())
      profile.invalidate();
    super.invalidate();
  }

  @Override
  protected void clean() {
    profileDao.deleteAll();
    super.clean();
  }

  protected MutableLiveData<String> sendRequest(String userId) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ProfileTask task = builder.fromTask(ProfileTask.class);
    task.setHandler(this);
    task.setCallBack(callBack);
    Bundle data = new Bundle();
    data.putString("action", "send");
    data.putString("user id", userId);
    TaskRequest request = builder
            .setAlias("User profile resolver")
            .setWillRestore(true)
            .setData(data)
            .build();
    postTask(request);
    return callBack;
  }

  protected MutableLiveData<String> acceptRequest(String userId) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ProfileTask task = builder.fromTask(ProfileTask.class);
    task.setHandler(this);
    task.setCallBack(callBack);
    Bundle data = new Bundle();
    data.putString("action", "accept");
    data.putString("user id", userId);
    TaskRequest request = builder
            .setAlias("User profile resolver")
            .setWillRestore(true)
            .setData(data)
            .build();
    postTask(request);
    return callBack;
  }

  protected MutableLiveData<String> rejectRequest(String userId) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ProfileTask task = builder.fromTask(ProfileTask.class);
    task.setHandler(this);
    task.setCallBack(callBack);
    Bundle data = new Bundle();
    data.putString("action", "reject");
    data.putString("user id", userId);
    TaskRequest request = builder
            .setAlias("User profile resolver")
            .setWillRestore(true)
            .setData(data)
            .build();
    postTask(request);
    return callBack;
  }

  protected MutableLiveData<String> cancelRequest(String userId) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    SerialTaskRequest.Builder builder = new SerialTaskRequest.Builder();
    ProfileTask task = builder.fromTask(ProfileTask.class);
    task.setHandler(this);
    task.setCallBack(callBack);
    Bundle data = new Bundle();
    data.putString("action", "cancel");
    data.putString("user id", userId);
    TaskRequest request = builder
            .setAlias("User profile resolver")
            .setWillRestore(true)
            .setData(data)
            .build();
    postTask(request);
    return callBack;
  }

  public static class ProfileTask extends SessionTask {
    private UserProfileProvider profileProvider;
    private MutableLiveData<String> callBack;

    public ProfileTask() {
    }

    @Override
    public void setHandler(SessionHandler handler) {
      super.setHandler(handler);
      profileProvider = (UserProfileProvider) handler;
    }

    public void setCallBack(MutableLiveData<String> callBack) {
      this.callBack = callBack;
    }

    private void deleteInLocal(String userId) {
      if (profileProvider != null) {
        FriendRequestAccessHandler fReqAccess = profileProvider.fReqAccess;
        fReqAccess.delete(userId);
      } else {
        DecadeDatabase db = DecadeDatabase.getInstance();
        UserBasicInfoDao userDao = db.getUserBasicInfoDao();
        FriendDao friendDao = db.getFriendDao();
        db.runInTransaction(() -> {
          FriendRequestItem oldItem = friendDao.findByUserId(userId);
          userDao.deleteById(oldItem.getUserInfoId());
        });
      }
    }

    @Override
    public void doTask() {
      Bundle data = getData();
      String action = data.getString("action");
      String userId = data.getString("user id");
      String status = null;
      switch (action) {
        case "send": {
          Call<ResponseBody> req = HttpCallSupporter.create(FriendApi.class)
                  .sendFriendRequest(userId);
          Response<ResponseBody> res = null;
          try {
            res = req.execute();
            status = res.code() == 200 ? "Success" : "Failed";
          } catch (IOException e) {
            status = "Failed";
            e.printStackTrace();
          }
          break;
        }
        case "accept": {
          Call<ResponseBody> req = HttpCallSupporter
                  .create(FriendApi.class)
                  .acceptFriendRequest(userId);
          Response<ResponseBody> res = null;
          try {
            res = req.execute();
            status = res.code() == 200 ? "Success" : "Failed";
          } catch (IOException e) {
            status = "Failed";
            e.printStackTrace();
          }
          deleteInLocal(userId);
          break;
        }
        case "reject": {
          Call<ResponseBody> req = HttpCallSupporter.create(FriendApi.class)
                  .rejectFriendRequest(userId);
          Response<ResponseBody> res = null;
          try {
            res = req.execute();
            status = res.code() == 200 ? "Success" : "Failed";
          } catch (IOException e) {
            status = "Failed";
            e.printStackTrace();
          }
          deleteInLocal(userId);
          break;
        }
        case "cancel": {
          Call<ResponseBody> req = HttpCallSupporter.create(FriendApi.class)
                  .cancelFriendRequest(userId);
          Response<ResponseBody> res = null;
          try {
            res = req.execute();
            status = res.code() == 200 ? "Success" : "Failed";
          } catch (IOException e) {
            status = "Failed";
            e.printStackTrace();
          }
          break;
        }
        default:
          assert false;
      }
      callBack.postValue(status);
    }
  }
}
