package com.example.socialmediaapp.application.session;

import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.api.ChatApi;
import com.example.socialmediaapp.api.MediaApi;
import com.example.socialmediaapp.api.entities.ChatDetailsBody;
import com.example.socialmediaapp.api.entities.UserBasicInfoBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.messenger.OnlineUserItem;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OnlineUsersProvider extends SessionHandler {
   private final FirebaseDatabase firebaseDatabase;
   private final UserPrincipal userPrincipal;
   private MutableLiveData<Update> itemUpdate;
   private DatabaseReference onlineRef;
   private DatabaseReference usersOnlineRef;
   private ChildEventListener usersOnlineEventListener;
   private HashMap<String, OnlineUserItem> onlineUserItems;
   private Handler mainThread = new Handler(Looper.getMainLooper());

   public OnlineUsersProvider() {
      firebaseDatabase = FirebaseDatabase.getInstance();
      userPrincipal = DecadeApplication.getInstance().onlineSessionHandler.userPrincipal;
      onlineUserItems = new HashMap<>();
      HashMap<String, Object> data = new HashMap<>();
      data.put("items", new ArrayList<>());
      data.put("consumed", 0);
      Update update = new Update(null, data);
      itemUpdate = new MutableLiveData<>(update);

      initOnlineUserSession();
   }

   public MutableLiveData<Update> getItemUpdate() {
      return itemUpdate;

   }

   @Override
   protected void invalidate() {
      super.invalidate();
      onlineRef.removeValue();
      usersOnlineRef.removeEventListener(usersOnlineEventListener);
   }

   private void initOnlineUserSession() {
      onlineRef = firebaseDatabase.getReference()
              .child("users")
              .child(userPrincipal.getUserId());
      onlineRef.setValue(true);
      onlineRef.onDisconnect().removeValue();
      usersOnlineRef = firebaseDatabase.getReference()
              .child("users");
      usersOnlineEventListener = new ChildEventListener() {
         @Override
         public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            String alias = snapshot.getKey();
            newUserOnline(alias);
         }

         @Override
         public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

         }

         @Override
         public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            String alias = snapshot.getKey();
            newUserOffline(alias);
         }

         @Override
         public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
      };
      usersOnlineRef.addChildEventListener(usersOnlineEventListener);
   }

   private void newUserOnline(String alias) {
      postTask(() -> {
         OnlineUserItem onlineUserItem = onlineUserItems.get(alias);
         if (onlineUserItem != null) {
            onlineUserItem.getIsOnline().postValue(true);
            return;
         }
         try {
            OnlineUserItem out = loadOnlineUserItem(alias);
            onlineUserItems.put(out.getChatInfo().getOther(), out);
            MutableLiveData<Boolean> isOnline = new MutableLiveData<>(true);
            out.setIsOnline(isOnline);
            mainThread.post(() -> {
               Update update = itemUpdate.getValue();
               ArrayList<OnlineUserItem> items = (ArrayList<OnlineUserItem>) update.data.get("items");
               items.add(out);
               update.data.put("update flag", 1);
               update.op = Update.Op.ADD;
               itemUpdate.setValue(update);
               update.op = null;
               itemUpdate.setValue(update);
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
   }

   private void newUserOffline(String alias) {
      postTask(() -> {
         OnlineUserItem onlineUserItem = onlineUserItems.get(alias);
         assert onlineUserItem != null;
         onlineUserItem.getIsOnline().postValue(false);
      });
   }

   private OnlineUserItem loadOnlineUserItem(String alias) throws IOException {
      Retrofit retrofit = DecadeApplication.getInstance().retrofit;
      Call<ChatDetailsBody> req = DecadeApplication.getInstance()
              .retrofit
              .create(ChatApi.class)
              .loadChatDetails(alias);
      Response<ChatDetailsBody> res = req.execute();
      assert res.code() == 200;
      ChatDetailsBody chatDetailsBody = res.body();
      UserBasicInfoBody userBasicInfoBody = chatDetailsBody.getUserBasicInfoBody();
      UserBasicInfoModel userBasicInfoModel = new UserBasicInfoModel();
      userBasicInfoModel.setFullname(userBasicInfoBody.getFullname());
      userBasicInfoModel.setId(userBasicInfoBody.getId());
      if (userBasicInfoBody.getAvatarId() != null) {
         Response<ResponseBody> img = retrofit.create(MediaApi.class).loadImage(userBasicInfoBody.getAvatarId()).execute();
         byte avatar[] = img.body().bytes();
         userBasicInfoModel.setAvatar(BitmapFactory.decodeByteArray(avatar, 0, avatar.length));
      }
      ChatInfo chatInfo = chatDetailsBody.getChatInfo();
      OnlineUserItem onlineUserItem = new OnlineUserItem();
      onlineUserItem.setChatInfo(chatInfo);
      onlineUserItem.setUserBasicInfo(userBasicInfoModel);
      return onlineUserItem;
   }
}
