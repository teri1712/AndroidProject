package com.example.socialmediaapp.application.session;

import android.os.Bundle;

import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

import java.util.Map;

public class MessageAccessHandler
        extends FcmAccessHandler<MessageItemModel, MessageItemBody> {
  protected final ChatSessionHandler chatHandler;

  public MessageAccessHandler(ChatSessionHandler chatHandler) {
    super(chatHandler.accessHelper,
            chatHandler.accessHelper
    );
    this.chatHandler = chatHandler;
  }

  @Override
  protected void onUpdateCompleted(Map<String, Object> data) {
    super.onUpdateCompleted(data);
    String type = (String) data.get("type");
    String fullname = (String) data.get("fullname");
    String viewContent = null;
    switch (type) {
      case "text":
        viewContent = (String) data.get("content");
        break;
      case "image":
        viewContent = fullname + " has sent an image";
        break;
      case "icon":
        viewContent = fullname + " has sent an icon";
        break;
      default:
        assert false;
        break;
    }
    Bundle msg = new Bundle();
    msg.putString("view content", viewContent);
    chatHandler.onNewMessage(msg);
  }
}
