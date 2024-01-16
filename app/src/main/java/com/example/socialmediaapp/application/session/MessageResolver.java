package com.example.socialmediaapp.application.session;

import com.example.socialmediaapp.api.ChatApi;
import com.example.socialmediaapp.api.debug.HttpCallSupporter;
import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.message.ChatDao;
import com.example.socialmediaapp.application.dao.SequenceDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.message.Chat;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class MessageResolver extends SessionHandler {
  private ChatThreadHandler messageThread;
  private OnlineSessionHandler onlineHandler;

  protected MessageResolver() {
  }

  public void setOnlineHandler(OnlineSessionHandler onlineHandler) {
    this.onlineHandler = onlineHandler;
  }

  @Override
  protected void invalidate() {
    messageThread.invalidate();
    messageThread = null;
    super.invalidate();
  }

  protected void init(UserPrincipal userPrincipal) {
    init();
    messageThread = new ChatThreadHandler(userPrincipal);
    boolean isNewAccount = userPrincipal.isNewAccount();
    if (isNewAccount) {
      try {
        initAndPullChatList();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public ChatThreadHandler getMessageSessionHandler() {
    return messageThread;
  }


  private void initAndPullChatList() throws IOException {
    DecadeDatabase db = DecadeDatabase.getInstance();
    ChatDao dao = db.getChatDao();

    Call<List<ChatInfo>> req = HttpCallSupporter
            .create(ChatApi.class)
            .initTokenAndPullChat();
    Response<List<ChatInfo>> res = req.execute();
    List<ChatInfo> body = res.body();
    HttpCallSupporter.debug(res);

    List<Chat> chats = new ArrayList<>();
    for (ChatInfo info : body) {
      Chat chat = new Chat();
      chat.setFullname(info.getFullname());
      chat.setMe(info.getMe());
      chat.setOther(info.getOther());
      chat.setId(info.getChatId());
    }
    dao.insertAll(chats);
  }

  public void intercept(MessageItemBody body) {
    post(() -> {
      String receiver = body.getChatInfo().getMe();
      if (!onlineHandler.ensureCorrect(receiver)) {
        return;
      }
      messageThread.onNewMessage(body);
    });
  }
}