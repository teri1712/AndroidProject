package com.example.socialmediaapp.application.session.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.apis.MediaApi;
import com.example.socialmediaapp.apis.MessageApi;
import com.example.socialmediaapp.apis.entities.MessageItemBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.MessageDao;
import com.example.socialmediaapp.application.database.AppDatabase;
import com.example.socialmediaapp.application.entity.IconMessageItem;
import com.example.socialmediaapp.application.entity.ImageMessageItem;
import com.example.socialmediaapp.application.entity.TextMessageItem;
import com.example.socialmediaapp.application.session.ChatSessionHandler;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MessageAccessHelper extends DataAccessHelper<MessageItem> {
   private Retrofit retrofit = ApplicationContainer.getInstance().retrofit;
   private AppDatabase db = ApplicationContainer.getInstance().database;
   private ChatSessionHandler chatSessionHandler;
   private MessageDao messageDao;
   private Integer chatId;
   private MessageDtoConvertor dtoConvertor;
   private LocalDataSupporter localDataSupporter;

   public MessageAccessHelper(Integer chatId) {
      this.chatId = chatId;
      messageDao = db.getMessageDao();
      dtoConvertor = new MessageDtoConvertor();
      localDataSupporter = new LocalDataSupporter();
   }

   private void updateNewMessageToDatabase(HashMap<String, Object> m) {
      com.example.socialmediaapp.application.entity.MessageItem messageItem = (com.example.socialmediaapp.application.entity.MessageItem) m.get("message item");
      ImageMessageItem imageMessageItem = (ImageMessageItem) m.get("image message item");
      TextMessageItem textMessageItem = (TextMessageItem) m.get("text message icon");
      IconMessageItem iconMessageItem = (IconMessageItem) m.get("icon message item");
      int msgId = (int) messageDao.insert(messageItem);
      if (textMessageItem != null) {
         textMessageItem.setMessageId(msgId);
         messageDao.insertTextMessageItem(textMessageItem);
      }
      if (imageMessageItem != null) {
         imageMessageItem.setMessageId(msgId);
         messageDao.insertImageMessageItem(imageMessageItem);
      }
      if (iconMessageItem != null) {
         iconMessageItem.setMessageId(msgId);
         messageDao.insertIconMessageItem(iconMessageItem);
      }
   }

   private void updateMessagesToDatabase(List<HashMap<String, Object>> msgs) {
      for (HashMap<String, Object> m : msgs) {
         updateNewMessageToDatabase(m);
      }
   }

   @Override
   public List<MessageItem> loadFromLocalStorage(HashMap<String, Object> query) {
      MessageItem lastItem = (MessageItem) query.get("last item");
      List<com.example.socialmediaapp.application.entity.MessageItem> items = messageDao.loadMessages(chatId, lastItem.getOrder());
      List<MessageItem> res = new ArrayList<>();
      for (com.example.socialmediaapp.application.entity.MessageItem msg : items) {
         MessageItem messageItem;
         if (Objects.equals("image", msg.getType())) {
            com.example.socialmediaapp.viewmodel.models.messenger.ImageMessageItem imageMessageItem = new com.example.socialmediaapp.viewmodel.models.messenger.ImageMessageItem();
            messageItem = imageMessageItem;
            ImageMessageItem imageMessageEntity = messageDao.loadImageMessage(msg.getId());
            imageMessageItem.setBitmap(localDataSupporter.loadImage(imageMessageEntity.getImageUri()));

         } else if (Objects.equals("text", msg.getType())) {
            com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem textMessageItem = new com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem();
            TextMessageItem textMessageEntity = messageDao.loadTextMessage(msg.getId());
            textMessageItem.setText(textMessageEntity.getContent());
            messageItem = textMessageItem;
         } else {
            messageItem = new com.example.socialmediaapp.viewmodel.models.messenger.IconMessageItem();
         }
         messageItem.setTime(msg.getTime());
         messageItem.setSender(msg.getSender());
         messageItem.setType(msg.getType());
         messageItem.setChatId(msg.getChatId());
         messageItem.setOrder(msg.getOrd());

         res.add(messageItem);
      }
      return res;
   }

   @Override
   public Bundle loadFromServer() throws IOException {
      Integer lastMessage = messageDao.lastMessageOrder(chatId);
      if (lastMessage == null) {
         lastMessage = -1;
      }
      List<HashMap<String, Object>> msgs = new ArrayList<>();
      Call<List<MessageItemBody>> messages = retrofit.create(MessageApi.class).loadMessages(chatId, lastMessage);
      Response<List<MessageItemBody>> res = messages.execute();
      List<MessageItemBody> messageItemBodies = res.body();
      for (MessageItemBody messageItemBody : messageItemBodies) {
         HashMap<String, Object> m = dtoConvertor.convertToMessageItem(messageItemBody, chatId);
         msgs.add(m);
      }
      db.runInTransaction(() -> updateMessagesToDatabase(msgs));
      return null;
   }

   @Override
   public MessageItem uploadToServer(Bundle query) throws IOException, FileNotFoundException {
      String type = query.getString("type");
      Response<MessageItemBody> response = null;
      switch (type) {
         case "text":
            String content = query.getString("content");
            RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
            response = retrofit.create(MessageApi.class).uploadTextMessage(contentBody, chatId).execute();
            break;
         case "image":
            String uriPath = query.getString("uri");
            Uri mediaContent = Uri.parse(uriPath);
            MultipartBody.Part mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, ApplicationContainer.getInstance().getContentResolver(), "media_content");
            response = retrofit.create(MessageApi.class).uploadImageMessage(mediaBody, chatId).execute();
            break;
         case "icon":
            response = retrofit.create(MessageApi.class).uploadIconMessage(chatId).execute();
            break;
         default:
            break;
      }

      MessageItemBody messageItemBody = response.body();
      HashMap<String, Object> m = dtoConvertor.convertToMessageItem(messageItemBody, chatId);
      db.runInTransaction(() -> {
         updateNewMessageToDatabase(m);
      });
      return dtoConvertor.convertToMessageModel((com.example.socialmediaapp.application.entity.MessageItem) m.get("message item"));
   }

   public MessageItem updateNewMessage(Bundle msg) throws IOException {
      String type = msg.getString("type");
      Response<MessageItemBody> response = null;
      com.example.socialmediaapp.application.entity.MessageItem messageItem = new com.example.socialmediaapp.application.entity.MessageItem();
      messageItem.setOrd(msg.getInt("ord"));
      messageItem.setType(msg.getString("type"));
      messageItem.setSender(msg.getString("sender"));
      messageItem.setTime(msg.getLong("time"));
      messageItem.setChatId(msg.getInt("chat id"));

      TextMessageItem tmi = null;
      ImageMessageItem imi = null;
      IconMessageItem icmi = null;
      switch (type) {
         case "text":
            tmi.setContent(msg.getString("content"));
         case "image":
            imi = new ImageMessageItem();
            imi.setImageUri(localDataSupporter.downloadImage(msg.getInt("media id")));
         case "icon":
            icmi = new IconMessageItem();
      }
      TextMessageItem textMessageItem = tmi;
      ImageMessageItem imageMessageItem = imi;
      IconMessageItem iconMessageItem = icmi;
      db.runInTransaction(() -> {
         int msgId = (int) messageDao.insert(messageItem);
         if (textMessageItem != null) {
            textMessageItem.setMessageId(msgId);
            messageDao.insertTextMessageItem(textMessageItem);
         }
         if (imageMessageItem != null) {
            imageMessageItem.setMessageId(msgId);
            messageDao.insertImageMessageItem(imageMessageItem);
         }
         if (iconMessageItem != null) {
            iconMessageItem.setMessageId(msgId);
            messageDao.insertIconMessageItem(iconMessageItem);
         }
      });
      return dtoConvertor.convertToMessageModel(messageItem);
   }

   @Override
   public void cleanAll() {
   }

   public LocalDataSupporter getLocalDataSupporter() {
      return localDataSupporter;
   }

   private class MessageDtoConvertor {
      public HashMap<String, Object> convertToMessageItem(MessageItemBody messageItemBody, Integer chatId) throws IOException {
         HashMap<String, Object> m = new HashMap<>();
         com.example.socialmediaapp.application.entity.MessageItem messageItem = new com.example.socialmediaapp.application.entity.MessageItem();
         m.put("message item", messageItem);
         messageItem.setChatId(chatId);
         messageItem.setOrd(messageItemBody.getOrd());
         messageItem.setSender(messageItemBody.getSender());
         messageItem.setTime(messageItemBody.getTime());

         String type = messageItemBody.getType();
         messageItem.setType(type);
         if (type.equals("image")) {
            Integer imageId = messageItemBody.getMediaId();
            ImageMessageItem imageMessageItem = new ImageMessageItem();
            imageMessageItem.setImageUri(localDataSupporter.downloadImage(imageId));
            m.put("image message item", imageMessageItem);
         } else if (type.equals("icon")) {
            IconMessageItem iconMessageItem = new IconMessageItem();
            m.put("icon message item", iconMessageItem);
         } else {
            TextMessageItem textMessageItem = new TextMessageItem();
            textMessageItem.setContent(messageItemBody.getContent());
            m.put("text message item", textMessageItem);
         }
         return m;
      }

      public MessageItem convertToMessageModel(com.example.socialmediaapp.application.entity.MessageItem msg) {
         MessageItem messageItem;
         if (Objects.equals("image", msg.getType())) {
            com.example.socialmediaapp.viewmodel.models.messenger.ImageMessageItem imageMessageItem = new com.example.socialmediaapp.viewmodel.models.messenger.ImageMessageItem();
            messageItem = imageMessageItem;
            ImageMessageItem imageMessageEntity = messageDao.loadImageMessage(msg.getId());
            imageMessageItem.setBitmap(localDataSupporter.loadImage(imageMessageEntity.getImageUri()));

         } else if (Objects.equals("text", msg.getType())) {
            com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem textMessageItem = new com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem();
            TextMessageItem textMessageEntity = messageDao.loadTextMessage(msg.getId());
            textMessageItem.setText(textMessageEntity.getContent());
            messageItem = textMessageItem;
         } else {
            messageItem = new com.example.socialmediaapp.viewmodel.models.messenger.IconMessageItem();
         }
         messageItem.setTime(msg.getTime());
         messageItem.setSender(msg.getSender());
         messageItem.setOrder(msg.getOrd());
         messageItem.setType(msg.getType());
         messageItem.setChatId(msg.getChatId());

         return messageItem;
      }
   }

   public class LocalDataSupporter {
      private Context context = ApplicationContainer.getInstance();
      private File chatDir;

      public LocalDataSupporter() {
         File dataDir = context.getFilesDir();
         File messengerDir = new File(dataDir, "messenger");
         chatDir = new File(messengerDir, "chat#" + chatId);
      }

      public String downloadImage(Integer imageId) throws IOException {
         File imageFile = new File(chatDir, "image#" + imageId + ".jpg");
         if (imageFile.exists()) {
            return imageFile.getAbsolutePath();
         }
         imageFile.createNewFile();
         FileOutputStream fos = new FileOutputStream(imageFile);
         Response<ResponseBody> img = retrofit.create(MediaApi.class).loadImage(imageId).execute();
         InputStream is = img.body().byteStream();
         byte[] buffer = new byte[2048];
         int cur = 0;
         long total = img.body().contentLength();
         while (cur != total) {
            int cnt = is.read(buffer, 0, Math.min((int) total - cur, 2048));
            cur += cnt;
            fos.write(buffer, 0, cnt);
         }
         is.close();
         fos.close();
         return imageFile.getAbsolutePath();
      }

      public Bitmap loadImage(String path) {
         return BitmapFactory.decodeFile(path);
      }
   }
}
