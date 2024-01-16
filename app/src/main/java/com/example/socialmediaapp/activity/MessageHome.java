package com.example.socialmediaapp.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.session.ChatThreadHandler;
import com.example.socialmediaapp.application.session.OnlineUsersProvider;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.ViewImageFragment;
import com.example.socialmediaapp.home.fragment.message.MainMessageFragment;
import com.example.socialmediaapp.services.TimePingService;
import com.example.socialmediaapp.viewmodel.MessageHomeViewModel;
import com.example.socialmediaapp.view.adapter.ChatAdapter;
import com.example.socialmediaapp.view.adapter.UserOnlineAdapter;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.messenger.OnlineUserItem;
import com.example.socialmediaapp.application.repo.core.utilities.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MessageHome extends AppCompatActivity {
  public class SpinLoadViewExtra extends RecyclerViewExtra {
    private CustomSpinningView loadSpin;

    public SpinLoadViewExtra() {
      super(new CustomSpinningView(MessageHome.this), Position.END);
      loadSpin = (CustomSpinningView) view;
    }

    @Override
    public void configure(View view) {
      loadSpin.setColor(Color.rgb(0x08, 0x66, 0xFF));
      int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(r, r);
      params.gravity = Gravity.CENTER_HORIZONTAL;
      loadSpin.setLayoutParams(params);

      viewModel.getLoadState().observe(MessageHome.this, new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
          if (aBoolean) {
            loadSpin.setVisibility(View.VISIBLE);
          } else {
            loadSpin.setVisibility(View.GONE);
          }
        }
      });
    }
  }

  private CircleButton settingButton, editButton;
  private EditText searchEditText;
  private RecyclerView onlineUserPanel;
  private RecyclerView recyclerView;
  private MessageHomeViewModel viewModel;
  private ChatThreadHandler threadHandler;
  private MediatorLiveData<Long> timeCounter;
  private ServiceConnection connection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      TimePingService.CounterBinder counterBinder = (TimePingService.CounterBinder) service;
      TimePingService timePingService = counterBinder.timePingService;
      timeCounter.addSource(timePingService.getTimeCount(), aLong -> timeCounter.setValue(aLong));
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    threadHandler = DecadeApplication.getInstance().onlineSessionHandler.getMsgResolver().getMessageSessionHandler();
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message_home);
    searchEditText = findViewById(R.id.search_edit_text);
    settingButton = findViewById(R.id.setting_button);
    editButton = findViewById(R.id.edit_button);
    onlineUserPanel = findViewById(R.id.online_user_panel);
    recyclerView = findViewById(R.id.chat_box_panel);

    init(threadHandler);

    timeCounter = new MediatorLiveData<>();
    Intent intent = new Intent(this, TimePingService.class);
    bindService(intent, connection, Context.BIND_AUTO_CREATE);
    checkForOpenChat(getIntent());
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    checkForOpenChat(intent);
  }

  private void checkForOpenChat(Intent intent) {
    if (intent == null) return;
    String chatId = intent.getStringExtra("chat id");
    if (chatId == null)
      return;
    String other = intent.getStringExtra("sender");
    String me = intent.getStringExtra("me");
    String fullname = intent.getStringExtra("fullname");
    ChatInfo chatInfo = new ChatInfo(chatId, me, other, fullname);

    openChatFragment(chatInfo);
  }

  private void init(ChatThreadHandler messageThreadHandler) {
    viewModel = new MessageHomeViewModel(messageThreadHandler);

    LiveData<Update> chatListUpdate = messageThreadHandler.getChatModelStore().getChatThreadUpdate();

    List<RecyclerViewExtra> viewExtras = new ArrayList<>();
    viewExtras.add(new SpinLoadViewExtra());
    ChatAdapter adapter = new ChatAdapter(this, (List<ChatSessionModel>) chatListUpdate.getValue().data.get("items"), new ArrayList<>(), viewExtras);
    recyclerView.setAdapter(adapter);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int pos = linearLayoutManager.findLastVisibleItemPosition();
        int total = recyclerView.getAdapter().getItemCount();

        if (pos + 3 >= total) {
          viewModel.load();
        }
      }
    });

    chatListUpdate.observe(this, update -> {
      Update.Op op = update.op;
      if (op == null) return;
      update.data.remove("update flag");
      Map<String, Object> data = update.data;
      int offset = (int) data.get("offset");
      if (op == Update.Op.ADD) {
        int length = (int) data.get("length");
        adapter.notifyItemRangeInserted(offset, length);
      } else if (op == Update.Op.REMOVE) {
        adapter.notifyItemRemoved(offset);
      }
    });
    viewModel.load();
    OnlineUsersProvider onlineUsersProvider = messageThreadHandler.getOnlineUsersProvider();
    LiveData<Update> itemUpdate = onlineUsersProvider.getItemUpdate();

    List<OnlineUserItem> onlineUserItems = (List<OnlineUserItem>) itemUpdate.getValue().data.get("items");
    UserOnlineAdapter userOnlineAdapter = new UserOnlineAdapter(this, onlineUserItems);
    onlineUserPanel.setAdapter(userOnlineAdapter);

    itemUpdate.observe(this, update -> {
      if (update.op == null) return;
      update.data.remove("update flag");
      userOnlineAdapter.notifyItemRangeInserted(onlineUserItems.size() - 1, 1);
    });
  }

  public LiveData<Long> getTimeCounter() {
    return timeCounter;
  }

  public MessageHomeViewModel getViewModel() {
    return viewModel;
  }

  @Override
  protected void onStart() {
    super.onStart();
    threadHandler.setOnForeground(true);
    Update chatSessionUpdate = threadHandler
            .getChatModelStore()
            .getChatThreadUpdate()
            .getValue();
    Update onlineUserUpdate = threadHandler
            .getOnlineUsersProvider()
            .getItemUpdate()
            .getValue();

    if (chatSessionUpdate.data.containsKey("update flag")) {
      recyclerView.getAdapter().notifyDataSetChanged();
      chatSessionUpdate.data.remove("update flag");
    }
    if (onlineUserUpdate.data.containsKey("update flag")) {
      onlineUserPanel.getAdapter().notifyDataSetChanged();
      onlineUserUpdate.data.remove("update flag");
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
    threadHandler.setOnForeground(false);
  }

  public void openChatFragment(ChatInfo chatInfo) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
            .beginTransaction();
    String tag = "chat id" + chatInfo.getChatId();
    fragmentTransaction.add(R.id.chat_fragment, MainMessageFragment.newInstance(chatInfo), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }

  public void openViewImageFragment(String imageUri) {
    FragmentTransaction fragmentTransaction = getSupportFragmentManager()
            .beginTransaction();
    String tag = "view image " + imageUri;
    fragmentTransaction.replace(R.id.view_image_fragment, ViewImageFragment.newInstance(imageUri), tag);
    fragmentTransaction.addToBackStack(tag);
    fragmentTransaction.commit();
  }
}