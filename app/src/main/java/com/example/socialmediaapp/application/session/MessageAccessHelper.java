package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import com.example.socialmediaapp.api.MessageApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.converter.ModelConvertor;
import com.example.socialmediaapp.application.dao.message.MessageDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.message.IconMessageItem;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;
import com.example.socialmediaapp.application.session.helper.DataUpdateHelper;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MessageAccessHelper
        extends DataAccessHelper<MessageItemModel>
        implements DataUpdateHelper<MessageItemModel> {
  private final DecadeDatabase db;
  private final MessageDao dao;
  private final String chatId;

  public MessageAccessHelper(String chatId) {
    super("Message access" + chatId);
    this.chatId = chatId;
    this.db = DecadeDatabase.getInstance();
    this.dao = db.getMessageDao();
  }

  protected void updateToLocal(Map<String, Object> m) {
    db.runInTransaction(() -> {
      MessageItem messageItem = (MessageItem) m.get("message item");
      ImageMessageItem imageMessageItem = (ImageMessageItem) m.get("image message item");
      TextMessageItem textMessageItem = (TextMessageItem) m.get("text message item");
      IconMessageItem iconMessageItem = (IconMessageItem) m.get("icon message item");

      int msgId = (int) dao.insert(messageItem);
      if (textMessageItem != null) {
        textMessageItem.setMessageId(msgId);
        dao.insertTextMessageItem(textMessageItem);
      }
      if (imageMessageItem != null) {
        imageMessageItem.setMessageId(msgId);
        dao.insertImageMessageItem(imageMessageItem);
      }
      if (iconMessageItem != null) {
        iconMessageItem.setMessageId(msgId);
        dao.insertIconMessageItem(iconMessageItem);
      }
      messageItem.setId(msgId);
    });
  }

  private List<MessageItemModel> flushToLocal(List<MessageItemBody> bodies) {
    List<MessageItemModel> list = new ArrayList<>();
    List<Map<String, Object>> msgs = new ArrayList<>();
    for (MessageItemBody body : bodies) {
      msgs.add(DtoConverter.convertToMessageItem(body));
    }
    for (Map<String, Object> m : msgs) {
      updateToLocal(m);
      list.add(ModelConvertor.convertToMessageModel((MessageItem) m.get("message item")));
    }
    return list;
  }

  @Override
  public List<MessageItemModel> loadFromLocal(Map<String, Object> query) {
    MessageItemModel lastItem = (MessageItemModel) query.get("last item");
    Long time = lastItem == null ? Long.MAX_VALUE : lastItem.getTime();
    List<MessageItem> items = dao.loadByTime(chatId, time);
    List<MessageItemModel> res = new ArrayList<>();
    for (MessageItem msg : items) {
      res.add(ModelConvertor.convertToMessageModel(msg));
    }
    return res;
  }


  @Override
  public Bundle loadFromServer() throws IOException {
    Integer lastMessage = dao.lastMessageOrder(chatId);
    Response<List<MessageItemBody>> res = HttpCallSupporter.create(MessageApi.class)
            .loadMessages(chatId, lastMessage)
            .execute();
    HttpCallSupporter.debug(res);
    List<MessageItemBody> bodies = res.body();
    flushToLocal(bodies);
    Bundle bundle = new Bundle();
    bundle.putInt("count loaded", bodies.size());
    return bundle;
  }

  @Override
  public List<MessageItemModel> update(Map<String, Object> data) throws IOException {
    MessageItemBody body = (MessageItemBody) data.get("item");
    List<MessageItemBody> bodies = new ArrayList<>();
    bodies.add(body);
    return flushToLocal(bodies);
  }

  @Override
  public void cleanAll() {
    //
  }
}
