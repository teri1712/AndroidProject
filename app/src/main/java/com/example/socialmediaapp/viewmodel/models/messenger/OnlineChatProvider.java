package com.example.socialmediaapp.viewmodel.models.messenger;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.ChatSessionHandler;
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

   public OnlineChatProvider(FirebaseDatabase firebaseDatabase) {
      this.firebaseDatabase = firebaseDatabase;
      accessEntries = new ArrayList<>();
   }

   public void apply(ChatSessionModel chatSessionModel) {
      ChatInfo chatInfo = chatSessionModel.getChatInfo();
      DatabaseReference ref = firebaseDatabase.getReference().child("chat").child(Integer.toString(chatInfo.getChatId())).child(chatInfo.getSender());
      DatabaseReference seen = ref.child("seen");
      DatabaseReference isTexting = ref.child("isTexting");
      DatabaseReference isActive = firebaseDatabase.getReference("user/" + chatInfo.getSender() + "/active");

      ChatSessionHandler chatSessionHandler = chatSessionModel.getChatBoxSessionHandler();
      OnlineChat onlineChat = chatSessionModel.getOnlineChat();
      MutableLiveData<Boolean> isTextingLiveData = new MutableLiveData<>();
      onlineChat.setIsTexting(isTextingLiveData);
      ValueEventListener seenListener = new ValueEventListener() {
         @Override
         public void onDataChange(@NonNull DataSnapshot snapshot) {
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
            chatSessionHandler.onUserOnlineStateChanged(snapshot.getValue(Boolean.class));
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
