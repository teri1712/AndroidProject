package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.application.MessageMonitorStore;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.message.MessageDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.message.IconMessageItem;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

import java.util.ArrayList;
import java.util.List;

public class MessageUploadHelper extends UploadHelper<MessageItemModel> {
  private final ChatSessionHandler chatHandler;
  private final MessageDao dao;
  private final DecadeDatabase db;

  public MessageUploadHelper(
          MessageAccessHandler accessHandler,
          Class<? extends UploadTask<MessageItemModel>> uploadTask) {
    super(accessHandler, uploadTask);
    chatHandler = accessHandler.chatHandler;
    db = DecadeDatabase.getInstance();
    dao = db.getMessageDao();
  }

  @Override
  public void uploadNewItem(Bundle data) {
    handler.post(() -> {
      Bundle newMsg = new Bundle();
      String type = data.getString("type");
      switch (type) {
        case "text":
          newMsg.putString("content", "You : " + data.getString("content"));
          break;
        case "image":
          newMsg.putString("content", "You has sent an image");
          break;
        case "icon":
          newMsg.putString("content", "You has sent an icon");
          break;
        default:
          assert false;
          break;
      }
      newMsg.putString("type", type);
      newMsg.putString("sender", "You");
      newMsg.putLong("time", data.getLong("time"));

      chatHandler.onNewMessage(newMsg);

      MessageItem unCommitted = updateUnCommittedInLocal(data);
      data.putInt("message id", unCommitted.getId());
      MessageMonitorStore.getInstance().create(unCommitted.getId());
      onItemUploaded("Success", ModelConvertor.convertToMessageModel(unCommitted));
      super.uploadNewItem(data);
    });
  }


  private MessageItem updateUnCommittedInLocal(Bundle data) {
    List<MessageItem> item = new ArrayList<>();
    String type = data.getString("type");
    db.runInTransaction(() -> {
      MessageItem messageItem = new MessageItem();
      messageItem.setMine(true);
      messageItem.setType(type);
      messageItem.setChatId(chatHandler.chatInfo.getChatId());
      messageItem.setTime(data.getLong("time"));
      item.add(messageItem);
      int msgId = (int) dao.insert(messageItem);

      switch (type) {
        case "text":
          TextMessageItem textMsg = new TextMessageItem();
          textMsg.setMessageId(msgId);
          textMsg.setContent(data.getString("content"));
          dao.insertTextMessageItem(textMsg);
          break;
        case "image":
          ImageMessageItem imageMsg = new ImageMessageItem();
          imageMsg.setMessageId(msgId);
          imageMsg.setImageUri(data.getString("uri"));
          imageMsg.setWidth(data.getInt("width"));
          imageMsg.setHeight(data.getInt("height"));
          dao.insertImageMessageItem(imageMsg);
          break;
        case "icon":
          IconMessageItem iconMsg = new IconMessageItem();
          iconMsg.setMessageId(msgId);
          dao.insertIconMessageItem(iconMsg);
          break;
      }
    });
    return item.get(0);
  }
}
