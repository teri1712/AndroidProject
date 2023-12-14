package com.example.socialmediaapp.application.session;


import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.apis.AuthenApi;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.viewmodel.models.messenger.OnlineChatProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.TreeMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnlineSessionHandler extends SessionHandler {

   public class UserProfileProvider {
      private Handler mainLooper = new Handler(Looper.getMainLooper());
      private SelfProfileSessionHandler selfProfile;
      private TreeMap<String, Integer> profiles;

      public UserProfileProvider() {
         profiles = new TreeMap<>();
      }

      public LiveData<SelfProfileSessionHandler> getSelfProfile() {
         MutableLiveData<SelfProfileSessionHandler> callBack = new MutableLiveData<>();
         post(() -> {
            if (selfProfile == null) {
               String self = userCredential.getAlias();
               profiles.put(self, createAndBindSelfProfile(self));
            }
            callBack.postValue(selfProfile);
         });
         return callBack;
      }

      public LiveData<Integer> getViewUserProfileSessionId(final String userAlias) {
         MutableLiveData<Integer> callBack = new MutableLiveData<>();
         post(() -> {
            Integer sid = profiles.get(userAlias);
            if (sid == null) {
               String self = userCredential.getAlias();
               if (userAlias.equals(self)) {
                  sid = createAndBindSelfProfile(self);
               } else {
                  ViewProfileSessionHandler p = new ViewProfileSessionHandler(userAlias);
                  sid = sessionRegistry.bind(p);
               }
               profiles.put(userAlias, sid);
            }
            callBack.postValue(sid);
         });
         return callBack;
      }

      private Integer createAndBindSelfProfile(String user) {
         selfProfile = new SelfProfileSessionHandler(user);
         Integer sid = sessionRegistry.bind(selfProfile);
         mainLooper.post(() -> userSession.getBindHelper().bind(selfProfile));
         return sid;
      }

      private void dispose() {
         profiles.clear();
         selfProfile = null;
      }

   }

   private SessionRepository sessionRepository;
   private UserCredential userCredential;
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private UserSessionHandler userSession;
   private UserProfileProvider userProfileProvider;
   private OnlineChatProvider onlineChatProvider;
   private MessageSessionHandler messageSessionHandler;
   private FirebaseDatabase firebaseDatabase;

   public OnlineSessionHandler(FirebaseDatabase firebaseDatabase) {
      super();
      this.firebaseDatabase = firebaseDatabase;
      id = 0;
      sessionRepository = new SessionRepository();
   }

   public OnlineChatProvider getOnlineChatProvider() {
      return onlineChatProvider;
   }

   public UserProfileProvider getUserProfileProvider() {
      return userProfileProvider;
   }

   public SessionRepository getSessionRepository() {
      return sessionRepository;
   }

   public MessageSessionHandler getMessageSessionHandler() {
      return messageSessionHandler;
   }

   public UserSessionHandler getUserSession() {
      return userSession;
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

   private void initUserSession() {
      userSession = new UserSessionHandler();
      sessionRegistry.bind(userSession);
      messageSessionHandler = new MessageSessionHandler(userCredential);
      sessionRegistry.bind(userSession);

      userProfileProvider = new UserProfileProvider();
      onlineChatProvider = new OnlineChatProvider(firebaseDatabase);
   }

   private void invalidateUserSession() {
      sessionRegistry.clear();
      onlineChatProvider.dispose();
      userProfileProvider.dispose();
   }

   private void handleNetworkIssue(Throwable t) {
   }

   private void onlineSessionStrategy() {
   }

   public MutableLiveData<String> authenticate(String username, String password) {
      MutableLiveData<String> callBack = new MutableLiveData<>();
      post(() -> {
         AuthenApi request = retrofit.create(AuthenApi.class);
         Call<ResponseBody> req = request.login(username, password);
         String status;
         try {
            Response<ResponseBody> res = req.execute();
            status = res.code() == 200 ? "Success" : res.errorBody().string();
            initUserSession();
         } catch (IOException e) {
            e.printStackTrace();
            status = "network issue";
         }
         callBack.postValue(status);
      });
      return callBack;
   }

   public MutableLiveData<String> signUp(String username, String password) {
      MutableLiveData<String> callBack = new MutableLiveData<>();
      post(() -> {
         AuthenApi request = retrofit.create(AuthenApi.class);
         Call<ResponseBody> req = request.signup(username, password);
         String status = null;
         try {
            Response<ResponseBody> res = req.execute();
            status = res.code() == 200 ? "Success" : res.errorBody().string();
            initUserSession();
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
         AuthenApi request = retrofit.create(AuthenApi.class);
         Call<ResponseBody> req = request.logout();
         try {
            Response<ResponseBody> res = req.execute();
            if (res.code() == 200) {
               callBack.postValue("Success");
            } else {
               callBack.postValue(res.errorBody().string());
            }
         } catch (IOException e) {
            e.printStackTrace();
            callBack.postValue("network issue");
         }
         invalidateUserSession();
      });
      return callBack;
   }
}
