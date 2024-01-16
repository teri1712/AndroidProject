package com.example.socialmediaapp.application.session;


import android.content.SharedPreferences;
import android.util.ArrayMap;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.AuthenApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.FriendRequestBody;
import com.example.socialmediaapp.api.entities.NotificationBody;
import com.example.socialmediaapp.api.entities.PrincipalBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class OnlineSessionHandler extends SessionHandler {

  private static OnlineSessionHandler onlineSession;
  private static Object lock = new Object();

  public static OnlineSessionHandler getInstance() {
    synchronized (lock) {
      if (onlineSession == null) {
        try {
          lock.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    return onlineSession;
  }

  public static void create(OnlineSessionHandler onlineSessionHandler) {
    synchronized (lock) {
      onlineSession = onlineSessionHandler;
      lock.notifyAll();
    }
  }

  // Will separate this component later.

  protected UserPrincipal userPrincipal;
  private MutableLiveData<UserPrincipal> principalLiveData;
  private UserProfileProvider profileProvider;
  private UserSessionHandler userHandler;
  private SharedPreferences sharedPreferences;
  private MessageResolver msgResolver;

  public OnlineSessionHandler() {
    super();
    principalLiveData = new MutableLiveData<>();
    sharedPreferences = DecadeApplication.getInstance().sharedPreferences;
    msgResolver = new MessageResolver();
  }

  protected void init() {
    msgResolver.setOnlineHandler(this);
    super.init();
  }

  /* synchronously get the principal*/
  public DeferredValue<UserPrincipal> getUserPrincipal() {
    DeferredValue<UserPrincipal> deffer = new DeferredValue<>();
    ensureThread(() -> deffer.set(userPrincipal));
    return deffer;
  }

  /* asynchronously get the principal for UI layer */
  public MutableLiveData<UserPrincipal> getPrincipalLiveData() {
    return principalLiveData;
  }

  public MessageResolver getMsgResolver() {
    return msgResolver;
  }

  public UserProfileProvider getProfileProvider() {
    return profileProvider;
  }

  public UserSessionHandler getUserHandler() {
    return userHandler;
  }

  protected boolean ensureCorrect(String receiver) {
    return userPrincipal != null && userPrincipal.getUserId().equals(receiver);
  }

  public void onNewNotification(NotificationBody body) {
    post(() -> {
      if (ensureCorrect(body.getReceiver())) {
        return;
      }
      FriendRequestBody requestBody = body.getFriendRequestBody();
      if (requestBody != null) {
        Map<String, Object> data = new ArrayMap<>();
        data.put("body", requestBody);
        userHandler.fReqAccess.updateNewItems(data);
      }
      Map<String, Object> data = new ArrayMap<>();
      data.put("body", body);
      userHandler.notifyAccess.updateNewItems(data);
    });
  }

  public void networkInterrupt(Throwable t) {
    post(() -> {
      handleNetworkIssue(t);
      interrupt();
    });
  }

  public void networkBack() {
    post(() -> {
      onlineSessionStrategy();
      resume();
    });
  }

  public void onRememberMeTokenChanged(String token) {
    post(() -> {
      SharedPreferences.Editor editor = sharedPreferences.edit();
      editor.putString("remember-me", token);
      editor.commit();
    });
  }

  private void syncInvalidateFirebaseToken() {
    Object dummy = new Object();
    FirebaseMessaging.getInstance()
            .deleteToken()
            .addOnCompleteListener(task -> {
              if (!task.isSuccessful()) {
                assert false;
              }
              synchronized (dummy) {
                userPrincipal.setFbToken(null);
                dummy.notify();
              }
            });
    synchronized (dummy) {
      if (userPrincipal.getFbToken() != null) {
        try {
          dummy.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }

  }

  private void syncGetFirebaseToken() {
    Object dummy = new Object();

    FirebaseMessaging.getInstance()
            .getToken()
            .addOnCompleteListener(task -> {
              if (!task.isSuccessful()) {
                assert false;
              }
              synchronized (dummy) {
                userPrincipal.setFbToken(task.getResult());
                dummy.notify();
              }
            });
    synchronized (dummy) {
      if (userPrincipal.getFbToken() == null) {
        try {
          dummy.wait();
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void initAndValidatePrincipal() {
    init();
    cleanUpPreviousSession();

    String rememberMeToken = sharedPreferences.getString("remember-me", null);
    String username = sharedPreferences.getString("username", null);
    String id = sharedPreferences.getString("id", null);
    String fbToken = sharedPreferences.getString("firebase-token", null);
    if (rememberMeToken != null) {
      userPrincipal = new UserPrincipal();
      userPrincipal.setUserId(id);
      userPrincipal.setUsername(username);
      userPrincipal.setNewAccount(false);
      syncGetFirebaseToken();
      if (!fbToken.equals(userPrincipal.getFbToken())) {

      }
      userPrincipal.setFbToken(fbToken);
      DecadeApplication
              .getInstance()
              .cookies
              .add(rememberMeToken);
      initUserSession();
    }
    principalLiveData.postValue(userPrincipal);
  }

  private void initUserSession() {
    userHandler = new UserSessionHandler();
    profileProvider = new UserProfileProvider(userPrincipal, userHandler);
    msgResolver.init(userPrincipal);
  }

  private void invalidateUserSession() {
    profileProvider.invalidate();
    msgResolver.invalidate();
    userHandler.invalidate();
    profileProvider = null;
    userHandler = null;

    syncInvalidateFirebaseToken();
    userPrincipal = null;
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.clear();
    editor.commit();
    cleanUpPreviousSession();
    System.out.println(5);
  }

  private void handleNetworkIssue(Throwable t) {
  }

  private void onlineSessionStrategy() {
  }

  public MutableLiveData<String> authenticate(String username, String password) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    post(() -> {
      AuthenApi request = HttpCallSupporter.create(AuthenApi.class);
      Call<ResponseBody> req = request.login(username, password);
      String status;
      try {
        Response<ResponseBody> res = req.execute();
        status = res.code() == 200 ? "Success" : res.errorBody().string();
        if (res.code() == 200) {
          initPrincipal();
        }
      } catch (IOException e) {
        e.printStackTrace();
        status = "network issue";
      }
      callBack.postValue(status);
    });
    return callBack;
  }

  private void initPrincipal() throws IOException {
    userPrincipal = new UserPrincipal();
    syncGetFirebaseToken();
    PrincipalBody principalBody = HttpCallSupporter
            .create(AuthenApi.class)
            .loadPrincipal("Bearer " + userPrincipal.getFbToken())
            .execute()
            .body();
    userPrincipal.setUserId(principalBody.getId());
    userPrincipal.setUsername(principalBody.getUsername());
    userPrincipal.setNewAccount(true);
    SharedPreferences.Editor editor = sharedPreferences.edit();
    editor.putString("id", userPrincipal.getUserId());
    editor.putString("username", userPrincipal.getUsername());
    editor.putString("firebase-token", userPrincipal.getFbToken());
    Set<String> cookies = DecadeApplication.getInstance().cookies;
    for (String cookie : cookies) {
      if (cookie.startsWith("remember-me")) {
        editor.putString("remember-me", cookie);
      }
    }
    editor.commit();
    initUserSession();
  }

  public MutableLiveData<String> signUp(String username, String password) {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    post(() -> {
      AuthenApi request = HttpCallSupporter.create(AuthenApi.class);
      Call<ResponseBody> req = request.signup(username, password);
      String status = null;
      try {
        Response<ResponseBody> res = req.execute();
        status = res.code() == 200 ? "Success" : res.errorBody().string();
        if (res.code() == 200) {
          initPrincipal();
        }
      } catch (IOException e) {
        e.printStackTrace();
        status = "network issue";
      }
      callBack.postValue(status);
    });
    return callBack;
  }

  public MutableLiveData<String> logout() {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    post(() -> {
      AuthenApi request = HttpCallSupporter.create(AuthenApi.class);
      Call<ResponseBody> req = request.logout();
      try {
        Response<ResponseBody> res = req.execute();
        if (res.code() == 200) {
          invalidateUserSession();
          DecadeApplication.getInstance().cookies.clear();
          callBack.postValue("Success");
        } else {
          callBack.postValue(res.errorBody().string());
        }
      } catch (IOException e) {
        assert false;
        e.printStackTrace();
        callBack.postValue("network issue");
      }
    });
    return callBack;
  }


  private void cleanUpPreviousSession() {
    DecadeDatabase database = DecadeDatabase.getInstance();
    File caches = DecadeApplication.getInstance().getCacheDir();
    File dataDir = DecadeApplication.getInstance().getFilesDir();
    for (File f : dataDir.listFiles()) {
      if (f.isFile()) {
        f.delete();
        continue;
      }
      cleanUpDirectory(f);
    }
    for (File f : caches.listFiles()) {
      if (f.isFile()) {
        f.delete();
        continue;
      }
      cleanUpDirectory(f);
    }
    database.getPostDao().deleteAllPost();
    database.getOrderPostDao().deleteAllPostAccess();
    database.getCommentDao().deleteAllComment();
    database.getCommentDao().deleteAllReplyComment();
    database.getOrderCommentDao().deleteAllCommentAccess();
    database.getOrderReplyDao().deleteAllReplyAccess();
    database.getUserBasicInfoDao().deleteAll();
    database.getChatDao().deleteAllChat();
    database.getChatDao().deleteAllOrderedChat();
    database.getMessageDao().deleteAllMessage();
    database.getNotificationDao().deleteAll();
    database.getNotificationDao().deleteNotifyDetails();
    database.getRegistryDao().deleteAll();
    database.getProfileDao().deleteAll();
    database.getFriendDao().deleteAll();
  }

  private void cleanUpDirectory(File dir) {
    for (File f : dir.listFiles()) {
      if (f.isFile()) {
        f.delete();
      } else if (f.isDirectory()) {
        cleanUpDirectory(f);
      }
    }
    dir.delete();
  }
}
