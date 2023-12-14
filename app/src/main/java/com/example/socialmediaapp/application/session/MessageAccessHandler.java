package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.application.session.helper.MessageAccessHelper;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem;
import com.example.socialmediaapp.viewmodel.models.repo.callback.DataEmit;

import java.io.IOException;
import java.util.HashMap;

public class MessageAccessHandler extends DataAccessHandler<MessageItem> {
   protected MessageAccessHelper msgAccessHelper;
   protected ChatSessionHandler chatSessionHandler;

   public MessageAccessHandler(ChatSessionHandler chatSessionHandler) {
      super(chatSessionHandler.messageAccessHelper);
      this.chatSessionHandler = chatSessionHandler;
      this.msgAccessHelper = chatSessionHandler.messageAccessHelper;
   }

   protected void init() {
      super.init();
      msgAccessHelper.setSession(this);
   }

   @Override
   public MutableLiveData<HashMap<String, Object>> uploadNewItem(Bundle data) {
      final MutableLiveData<HashMap<String, Object>> callBack = new MutableLiveData<>();
      post(() -> {
         postToWorker(() -> {
            HashMap<String, Object> result = new HashMap<>();
            try {
               MessageItem item = dataAccessHelper.uploadToServer(data);
               result.put("status", "Success");
               result.put("item", item);

               Bundle newMsg = new Bundle();
               String type = item.getType();
               String sender = item.getSender();

               if (type.equals("text")) {
                  newMsg.putString("content", ((TextMessageItem) item).getText());
               } else if (type.equals("image")) {
                  newMsg.putString("content", sender + " has sent an image");
               } else {
                  newMsg.putString("content", sender + " has sent an icon");
               }
               newMsg.putString("sender", sender);
               newMsg.putInt("chat id", item.getChatId());
               newMsg.putLong("time", item.getTime());

               post(() -> chatSessionHandler.postMessageProcess(newMsg));

            } catch (IOException e) {
               result.put("status", "Failed");
               e.printStackTrace();
            }
            callBack.postValue(result);
         });
      });
      return callBack;
   }

   protected void updateNewMessage(Bundle msg) {
      postToWorker(() -> {
         try {
            MessageItem item = msgAccessHelper.updateNewMessage(msg);
            post(() -> {
               HashMap<String, Object> m = new HashMap<>();
               m.put("item", item);
               dataEmitter.onNext(new DataEmit(m, "Success", "new message"));
            });
         } catch (IOException e) {
            e.printStackTrace();
         }
      });
   }
}
