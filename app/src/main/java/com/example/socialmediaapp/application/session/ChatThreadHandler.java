package com.example.socialmediaapp.application.session;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.ArrayMap;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.api.entities.MessageItemBody;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.dao.message.ChatDao;
import com.example.socialmediaapp.application.database.DecadeDatabase;
import com.example.socialmediaapp.application.entity.message.Chat;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.chat.OnlineChatProvider;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.utils.ImageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatThreadHandler extends SessionHandler {
  protected final ChatSessionModelStore chatModelStore;
  private final Handler mainThread = new Handler(Looper.getMainLooper());
  private final OnlineUsersProvider onlineUsersProvider;
  private final OnlineChatProvider onlineChatProvider;
  private final DecadeDatabase db;
  private final ChatDao dao;
  private final ReadCommitted readCommitted;

  /* persist list chat */
  private final List<Chat> listChat;
  private boolean isOnForeground;


  public ChatThreadHandler(UserPrincipal userPrincipal) {
    this.chatModelStore = new ChatSessionModelStore();
    this.onlineChatProvider = new OnlineChatProvider(userPrincipal);
    this.onlineUsersProvider = new OnlineUsersProvider();
    this.listChat = new ArrayList<>();
    this.readCommitted = new ReadCommitted();
    this.db = DecadeDatabase.getInstance();
    this.dao = db.getChatDao();
    this.isOnForeground = false;
    init();
  }

  /* Generate and show notification if not on foreground*/
  private void updateNotification(Bundle msg) {
    String avatarUri = (String) msg.get("avatar uri");

    DecadeApplication application = DecadeApplication.getInstance();
    Bitmap avatar = null;
    Bitmap cropped = null;
    try {
      avatar = application.picasso.load(avatarUri).get();
      cropped = ImageUtils.cropCircle(avatar);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Intent intent = new Intent(application, MessageHome.class);

    intent.putExtra("sender", msg.getString("sender"));
    intent.putExtra("fullname", msg.getString("fullname"));
    intent.putExtra("chat id", msg.getString("chat id"));

    PendingIntent pendingIntent = PendingIntent.getActivity(
            application
            , 0
            , intent
            , PendingIntent.FLAG_UPDATE_CURRENT);
    RemoteViews remoteView = new RemoteViews(application.getPackageName(), R.layout.notification_messenger_layout);
    if (cropped != null) {
      remoteView.setImageViewBitmap(R.id.avatar_view, avatar);
    } else {
      remoteView.setImageViewResource(R.id.avatar_view, R.drawable.avatar);
    }

    remoteView.setTextViewText(R.id.fullname, msg.getString("fullname"));
    remoteView.setTextViewText(R.id.text_content, msg.getString("view content"));

    NotificationCompat.Builder builder = new NotificationCompat.Builder(application, "decade_message")
            .setSmallIcon(R.drawable.facebook_24)
            .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
            .setContentTitle("You have new message")
            .setCustomContentView(remoteView)
            .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true);

    NotificationManager notificationManager = (NotificationManager) application.getSystemService(Context.NOTIFICATION_SERVICE);
    int notiId = msg.getString("sender").hashCode();
    notificationManager.notify(notiId, builder.build());

  }

  public OnlineUsersProvider getOnlineUsersProvider() {
    return onlineUsersProvider;
  }

  public void setOnForeground(boolean onForeground) {
    post(() -> isOnForeground = onForeground);
  }

  public ChatSessionModelStore getChatModelStore() {
    return chatModelStore;
  }

  @Override
  protected void init() {
    super.init();
    onlineUsersProvider.init();
  }

  @Override
  protected void invalidate() {
    onlineUsersProvider.invalidate();
    onlineChatProvider.dispose();
    super.invalidate();
  }

  /* place where message flow into */
  protected void onNewMessage(MessageItemBody body) {
    ChatInfo chatInfo = body.getChatInfo();
    ChatSessionHandler chatHandler = chatModelStore
            .getChatModel(chatInfo)
            .getChatHandler();
    readCommitted.acquire();
    chatHandler.accessHandler.onNewMessage(body);
  }

  /* Reorder the chat threads */
  /* place where flowed message echo back*/
  public void onMessageProcessed(Bundle msg) {
    String chatId = msg.getString("chat id");
    String fullname = msg.getString("fullname");
    String other = msg.getString("other");
    String me = msg.getString("me");
    boolean isMine = msg.getBoolean("mine", false);

    ChatInfo chatInfo = new ChatInfo(chatId, me, other, fullname);
    final ChatSessionModel chatSessionModel = chatModelStore.getChatModel(chatInfo);
    final MutableLiveData<Update> liveData = chatModelStore.chatThreadUpdate;
    final Update update = liveData.getValue();
    final List<ChatSessionModel> listModels = (List<ChatSessionModel>) update.data.get("items");

    for (int i = 0; i < listChat.size(); i++) {
      Chat chat = listChat.get(i);
      if (chat.getId().equals(chatId)) {
        final int offset = i;
        listChat.remove(offset);
        mainThread.post(new Runnable() {
          @Override
          public void run() {
            listModels.remove(offset);
            update.op = Update.Op.REMOVE;
            update.data.put("offset", offset);
            update.data.put("update flag", 1);
            liveData.setValue(update);
            update.op = null;
            liveData.setValue(update);
          }
        });
      }
    }
    mainThread.post(new Runnable() {
      @Override
      public void run() {
        listModels.add(0, chatSessionModel);
        update.data.put("offset", 0);
        update.data.put("length", 1);
        update.data.put("update flag", 1);
        update.op = Update.Op.ADD;
        liveData.setValue(update);
        update.op = null;
        liveData.setValue(update);
      }
    });
    if (!isMine && !isOnForeground) {
      updateNotification(msg);
    }
    if (msg.containsKey("update")) {
      readCommitted.commit();
    }
  }

  /* Load request from UI layer */

  public LiveData<String> loadChat() {
    MutableLiveData<String> callBack = new MutableLiveData<>();
    readCommitted.doRead(() -> {
      Long bound = listChat.isEmpty()
              ? Long.MAX_VALUE
              : listChat.get(listChat.size() - 1).getLastMsgTime();

      List<Chat> chats = dao.findChatByOrder(bound);
      List<ChatSessionModel> chatModels = new ArrayList<>();

      for (Chat chat : chats) {
        ChatInfo chatInfo = new ChatInfo();
        chatInfo.setFullname(chat.getFullname());
        chatInfo.setMe(chatInfo.getMe());
        chatInfo.setOther(chatInfo.getOther());
        chatInfo.setChatId(chat.getId());
        ChatSessionModel model = chatModelStore.getChatModel(chatInfo);
        chatModels.add(model);
      }

      final int offset = listChat.size();
      final int length = chats.size();

      listChat.addAll(chats);

      mainThread.post(() -> {
        if (length == 0) {
          callBack.setValue("End");
          return;
        }
        callBack.setValue("Success");
        MutableLiveData<Update> liveData = chatModelStore.chatThreadUpdate;
        Update update = liveData.getValue();
        List<ChatSessionModel> listModels = (List<ChatSessionModel>) update.data.get("items");
        listModels.addAll(chatModels);
        update.data.put("offset", offset);
        update.data.put("length", length);
        update.op = Update.Op.ADD;
        update.data.put("update flag", 1);
        liveData.setValue(update);
        update.op = null;
        liveData.setValue(update);
      });
    });
    return callBack;
  }

  public class ChatSessionModelStore {
    final private MutableLiveData<Update> chatThreadUpdate;
    final private Map<String, ChatSessionModel> s;

    public LiveData<ChatSessionModel> findChatModel(ChatInfo chatInfo) {
      MutableLiveData<ChatSessionModel> callback = new MutableLiveData<>();
      post(() -> {
        callback.postValue(getChatModel(chatInfo));
      });
      return callback;
    }

    public ChatSessionModelStore() {
      Map<String, Object> data = new ArrayMap<>();
      data.put("items", new ArrayList<>());
      this.s = new HashMap<>();
      this.chatThreadUpdate = new MutableLiveData<>(new Update(null, data));
    }

    protected ChatSessionModel getChatModel(ChatInfo chatInfo) {
      ChatSessionModel chatSessionModel = s.get(chatInfo.getChatId());
      if (chatSessionModel == null) {
        chatSessionModel = create(chatInfo);
      }
      return chatSessionModel;
    }

    private ChatSessionModel create(ChatInfo chatInfo) {
      ChatSessionHandler chatSessionHandler =
              new ChatSessionHandler(chatInfo, ChatThreadHandler.this);

      ChatSessionModel chatSessionModel = new ChatSessionModel(chatSessionHandler);

      s.put(chatSessionHandler.chatInfo.getChatId(), chatSessionModel);
      onlineChatProvider.apply(chatSessionModel);

      return chatSessionModel;
    }

    public MutableLiveData<Update> getChatThreadUpdate() {
      return chatThreadUpdate;
    }
  }

  /*  For the load chats read the committed version
   *   */
  private class ReadCommitted {
    private int unCommits;
    private Runnable readWait;

    private ReadCommitted() {
      unCommits = 0;
      readWait = null;
    }

    private void acquire() {
      unCommits++;
    }

    private void commit() {
      unCommits--;
      if (unCommits == 0) {
        if (readWait != null) {
          readWait.run();
          readWait = null;
        }
      }
    }

    private void doRead(Runnable run) {
      assert readWait == null;
      if (unCommits != 0) {
        readWait = run;
      } else {
        run.run();
      }
    }
  }
}
