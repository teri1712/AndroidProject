package com.example.socialmediaapp.application.session;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.api.CommentApi;
import com.example.socialmediaapp.api.MessageApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.CommentBody;
import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.MessageMonitorStore;
import com.example.socialmediaapp.application.converter.DtoConverter;
import com.example.socialmediaapp.application.converter.HttpBodyConverter;
import com.example.socialmediaapp.application.dao.message.MessageDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.comment.Comment;
import com.example.socialmediaapp.application.entity.comment.OrderedComment;
import com.example.socialmediaapp.application.entity.message.IconMessageItem;
import com.example.socialmediaapp.application.entity.message.ImageMessageItem;
import com.example.socialmediaapp.application.entity.message.MessageItem;
import com.example.socialmediaapp.application.entity.message.TextMessageItem;
import com.example.socialmediaapp.application.network.TaskDetails;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

import java.io.IOException;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MessageUploadTask extends UploadTask<MessageItemModel> {
  private MessageDao dao;
  private DecadeDatabase db;

  public MessageUploadTask() {
    super();
    db = DecadeDatabase.getInstance();
    dao = db.getMessageDao();
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

  @Override
  protected void onTaskPrepare(TaskDetails details) {
    super.onTaskPrepare(details);
    String pendId = details.getId();
    Bundle data = details.getData();
    Integer msgId = data.getInt("message id");
    dao.updatePendId(pendId, msgId);
    MessageMonitorStore.getInstance().bind(msgId, details.getMonitor());
  }

  @Override
  protected void onTaskCompleted() {
    super.onTaskCompleted();
    Integer msgId = getData().getInt("message id");
    MessageMonitorStore.getInstance().onCompleteSendMessage(msgId);
  }

  @Override
  public void doTask() {
    ContentResolver resolver = DecadeApplication.getInstance().getContentResolver();

    Bundle data = getData();
    String type = data.getString("type");
    Long time = data.getLong("time");
    String chatId = data.getString("chat id");

    try {
      Call<MessageItemBody> req = null;
      switch (type) {
        case "text": {
          String content = data.getString("content");
          RequestBody contentBody = HttpBodyConverter.getTextRequestBody(content);
          req = HttpCallSupporter.create(MessageApi.class).uploadMessage(contentBody, chatId, time);
          break;
        }
        case "image": {
          String uriPath = data.getString("uri");
          Uri mediaContent = Uri.parse(uriPath);
          MultipartBody.Part mediaBody = HttpBodyConverter.getMultipartBody(mediaContent, resolver, "media_content");
          req = HttpCallSupporter.create(MessageApi.class).uploadImage(mediaBody, chatId, time);
          break;
        }
        case "icon": {
          req = HttpCallSupporter.create(MessageApi.class).uploadIcon(chatId, time);
          break;
        }
        default:
          assert false;
          break;
      }
      Response<MessageItemBody> res = req.execute();
      HttpCallSupporter.debug(res);

      MessageItemBody body = res.body();
      Map<String, Object> m = DtoConverter.convertToMessageItem(body);
      updateToLocal(m);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {

    }
  }
}
