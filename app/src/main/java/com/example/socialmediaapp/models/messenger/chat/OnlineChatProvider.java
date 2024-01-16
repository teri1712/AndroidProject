package com.example.socialmediaapp.models.messenger.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.ChatSessionHandler;
import com.example.socialmediaapp.application.session.UserPrincipal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OnlineChatProvider {
   private class OnlineAccessEntry {
      private DatabaseReference databaseReference;
      private ValueEventListener valueEventListener;

      public OnlineAccessEntry(DatabaseReference databaseReference, ValueEventListener valueEventListener) {
         this.databaseReference = databaseReference;
         this.valueEventListener = valueEventListener;
      }
   }

   private FirebaseDatabase firebaseDatabase;
   private List<OnlineAccessEntry> accessEntries;
   private UserPrincipal userPrincipal;

   public OnlineChatProvider(UserPrincipal userPrincipal) {
      this.userPrincipal = userPrincipal;
      firebaseDatabase = FirebaseDatabase.getInstance();
      accessEntries = new ArrayList<>();
   }

   public void apply(ChatSessionModel chatSessionModel) {
      ChatInfo chatInfo = chatSessionModel.getChatInfo();
      DatabaseReference ref = firebaseDatabase.getReference().child("chat")
              .child(chatInfo.getChatId())
              .child(chatInfo.getOther());
      DatabaseReference seen = ref.child("seen");
      DatabaseReference isTexting = ref.child("isTexting");
      DatabaseReference isActive = firebaseDatabase.getReference("users")
              .child(chatInfo.getOther());
      ChatSessionHandler chatSessionHandler = chatSessionModel.getChatHandler();
      MutableLiveData<Boolean> isTextingLiveData = new MutableLiveData<>(false);
      DatabaseReference meTextingRef = firebaseDatabase.getReference()
              .child("chat")
              .child(chatInfo.getChatId())
              .child(userPrincipal.getUserId())
              .child("isTexting");
      meTextingRef.onDisconnect().setValue(false);
      chatSessionModel.initOnlineTexting(meTextingRef, isTextingLiveData);

      ValueEventListener seenListener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.getValue() == null) return;
            chatSessionHandler.onMessageSeen(snapshot.getValue(Long.class));
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {
         }
      };
      seen.addValueEventListener(seenListener);
      accessEntries.add(new OnlineAccessEntry(seen, seenListener));
      ValueEventListener isTextListener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (snapshot.getValue() == null) return;
            isTextingLiveData.setValue(snapshot.getValue(Boolean.class));
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
      };
      isTexting.addValueEventListener(isTextListener);
      accessEntries.add(new OnlineAccessEntry(isTexting, isTextListener));
      ValueEventListener isActiveListener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
            chatSessionHandler.onUserOnlineStateChanged(snapshot.getValue() != null);
         }

         @Override
         public void onCancelled(@NonNull DatabaseError error) {

         }
      };
      isActive.addValueEventListener(isActiveListener);
      accessEntries.add(new OnlineAccessEntry(isActive, isActiveListener));
   }

   public void dispose() {
      for (OnlineAccessEntry onlineAccessEntry : accessEntries) {
         onlineAccessEntry.databaseReference.removeEventListener(onlineAccessEntry.valueEventListener);
      }
      accessEntries.clear();
   }
}
