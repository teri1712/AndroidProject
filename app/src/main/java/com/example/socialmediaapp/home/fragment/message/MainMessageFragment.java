package com.example.socialmediaapp.home.fragment.message;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.repo.core.MessageUploadAdapter;
import com.example.socialmediaapp.application.session.ChatSessionHandler;
import com.example.socialmediaapp.application.session.ChatThreadHandler;
import com.example.socialmediaapp.models.messenger.chat.TextingUpdater;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.button.RoundedButton;
import com.example.socialmediaapp.view.button.UserActiveView;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.viewmodel.messenger.MainMessageFragmentViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.messenger.MessageFragmentViewModel;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.chat.OnlineChat;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MainMessageFragment extends Fragment {
  public class SendActionConfigurator implements MessageFragment.ConfigureExtra {
    @Override
    public void apply(View root, MessageFragmentViewModel fragmentViewModel) {
      MessageUploadAdapter uploadAdapter = new MessageUploadAdapter();
      fragmentViewModel.getRepo().setUploadAdapter(uploadAdapter);

      sendAction = data -> {
        try {
          /* my bad */
          Thread.sleep(10);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        long time = System.currentTimeMillis();
        data.putLong("time", time);
        LiveData<String> callBack = uploadAdapter.sendMessage(data);
        callBack.observe(getViewLifecycleOwner(), s -> {
        });
        return callBack;
      };
      initOnclick();
    }
  }

  public class ScrollButtonConfigurator implements MessageFragment.ConfigureExtra {
    private RecyclerView recyclerView;
    private boolean onScreen;

    @Override
    public void apply(View root, MessageFragmentViewModel fragmentViewModel) {
      recyclerView = root.findViewById(R.id.message_panel);
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

          int pos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
          if (pos <= 4) {
            if (!onScreen) return;
            onScreen = false;
            scrollDownButton.animate().alpha(0).setDuration(200).withEndAction(() -> {
              scrollDownButton.setTranslationY(0);
              scrollDownButton.setVisibility(View.GONE);
            }).start();
          } else {
            if (onScreen) return;
            onScreen = true;
            scrollDownButton.setVisibility(View.VISIBLE);
            scrollDownButton.animate().alpha(1).translationY(-30).setDuration(200).start();
          }
        }
      });
      configScrollButton();
    }

    private void configScrollButton() {
      scrollDownButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

          int pos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
          if (pos >= 10) {
            recyclerView.scrollToPosition(10);
          }
          recyclerView.smoothScrollToPosition(0);
        }
      });
    }
  }


  public MainMessageFragment() {
  }

  public static MainMessageFragment newInstance(ChatInfo chatInfo) {
    Bundle args = new Bundle();
    args.putString("chat id", chatInfo.getChatId());
    args.putString("other", chatInfo.getOther());
    args.putString("me", chatInfo.getMe());
    args.putString("fullname", chatInfo.getFullname());
    MainMessageFragment fragment = new MainMessageFragment();
    fragment.setArguments(args);
    return fragment;
  }

  private ChatInfo chatInfo;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    String chatId = args.getString("chat id");
    String other = args.getString("other");
    String me = args.getString("me");
    String fullname = args.getString("fullname");
    chatInfo = new ChatInfo(chatId, me, other, fullname);

    imageSelectorFragment = (ImageSelectorFragment) getChildFragmentManager().findFragmentByTag("image selector");
    if (imageSelectorFragment == null) {
      imageSelectorFragment = new ImageSelectorFragment();
    }
  }

  private EditText messsageEditText;
  private CircleButton selectImage, sendButton, selectIcon, scrollDownButton;
  private View root;
  private LiveData<String> sessionState;
  private MainMessageFragmentViewModel viewModel;
  private Function<Bundle, LiveData<String>> sendAction;
  private CircleButton backButton;
  private UserActiveView avatarView;
  private TextView fullname, onlineState;
  private Observer<Long> timeObserver;
  private LiveData<Long> timeCounter;
  private View funcPanel;
  private CircleButton nextButton;
  private RoundedButton sendImageButton;
  private PopupWindow sendImageButtonPopup;
  private ImageSelectorFragment imageSelectorFragment;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    root = inflater.inflate(R.layout.fragment_main_message, container, false);
    selectImage = root.findViewById(R.id.select_image);
    selectIcon = root.findViewById(R.id.emoji_button);
    sendButton = root.findViewById(R.id.send_button);
    messsageEditText = root.findViewById(R.id.message_edit_text);
    scrollDownButton = root.findViewById(R.id.scroll_down_button);
    backButton = root.findViewById(R.id.back_button);
    avatarView = root.findViewById(R.id.avatar_view);
    onlineState = root.findViewById(R.id.online_state);
    fullname = root.findViewById(R.id.fullname);
    funcPanel = root.findViewById(R.id.function_panel);
    nextButton = root.findViewById(R.id.next_button);

    View sendImageButtonLayout = inflater.inflate(R.layout.view_send_image_button, null);
    sendImageButtonPopup = new PopupWindow(sendImageButtonLayout,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT);
    sendImageButton = sendImageButtonLayout.findViewById(R.id.send_image_button);

    messsageEditText.setTextColor(Color.WHITE);
    nextButton.setFocusable(false);
    nextButton.setFocusableInTouchMode(false);
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        funcPanel.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.GONE);
      }
    });
    messsageEditText.setClickable(true);
    messsageEditText.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        funcPanel.setVisibility(View.GONE);
        nextButton.setVisibility(View.VISIBLE);
      }
    });
    messsageEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View view, boolean b) {
        if (b) {
          funcPanel.setVisibility(View.GONE);
          nextButton.setVisibility(View.VISIBLE);
        }
      }
    });
    sendButton.setFocusableInTouchMode(false);
    sendButton.setFocusable(false);
    ChatThreadHandler msgThreadHandler = DecadeApplication
            .getInstance()
            .onlineSessionHandler
            .getMsgResolver()
            .getMessageSessionHandler();
    ChatThreadHandler.ChatSessionModelStore chatStore = msgThreadHandler.getChatModelStore();
    LiveData<ChatSessionModel> chatModel = chatStore.findChatModel(chatInfo);
    initViewModel(chatModel);
    initMessageFragment(chatModel);

    chatModel.observe(getViewLifecycleOwner(), model -> initOnclick());
    chatModel.observe(getViewLifecycleOwner(), model -> initOnlineValues(model));
    chatModel.observe(getViewLifecycleOwner(), model -> initTextAction(model));
    return root;
  }

  public MainMessageFragmentViewModel getViewModel() {
    return viewModel;
  }

  private void initOnlineValues(ChatSessionModel chatSessionModel) {
    UserBasicInfoModel other = chatSessionModel.getOther();
    fullname.setText(other.getFullname());
    avatarView.setBackgroundContent(new BitmapDrawable(getResources(), other.getScaled()), -1);
    OnlineChat onlineChat = chatSessionModel.getOnlineChat();

    timeObserver = aLong -> {
      int m = (int) ((aLong - onlineChat.getOffTime()) / (60 * 1000));
      if (m >= 60) {
        avatarView.setUserState(UserActiveView.INACTIVE);
      } else if (m >= 1) {
        avatarView.setUserState(new UserActiveView.UserTimeActive(m, getContext()));
      }
      if (m >= 1) {
        onlineState.setText("offline");
      }
    };
    timeCounter = ((MessageHome) getContext()).getTimeCounter();
    onlineChat.getIsActive().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean aBoolean) {
        if (aBoolean) {
          avatarView.setUserState(UserActiveView.ACTIVE);
          onlineState.setText("online");
          timeCounter.removeObserver(timeObserver);
        } else {
          timeCounter.observe(getViewLifecycleOwner(), timeObserver);
        }
      }
    });
  }

  private void initTextAction(ChatSessionModel chatSessionModel) {
    TextingUpdater textingUpdater = chatSessionModel.getTextingUpdater();
    LiveData<String> msgContent = viewModel.getMsgContent();
    msgContent.observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        if (!s.isEmpty()) {
          textingUpdater.doTexting();
        } else {
          textingUpdater.stopTexting();
        }
      }
    });
  }

  private void initMessageFragment(LiveData<ChatSessionModel> chatSessionModelLiveData) {
    FragmentTransaction fTran = getChildFragmentManager().beginTransaction();

    List<MessageFragment.ConfigureExtra> configureExtras = new ArrayList<>();
    configureExtras.add(new SendActionConfigurator());
    configureExtras.add(new ScrollButtonConfigurator());

    List<RecyclerViewExtra> extras = new ArrayList<>();

    MessageFragment messageFragment = new MessageFragment(chatSessionModelLiveData, configureExtras, extras);
    fTran.replace(R.id.message_fragment_container, messageFragment);
    fTran.commit();
  }

  private void initViewModel(LiveData<ChatSessionModel> chatModel) {
    viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null))
            .get(MainMessageFragmentViewModel.class);
    viewModel.getMsgContent().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        if (!Objects.equals(s, messsageEditText.getText().toString())) {
          messsageEditText.setText(s);
        }
        viewModel.getMsgContent().removeObserver(this);
      }
    });
    messsageEditText.setText(viewModel.getMsgContent().getValue());
    messsageEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        viewModel.getMsgContent().setValue(editable.toString());
      }
    });
    viewModel.getMsgContent().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        if (s.isEmpty()) {
          sendButton.setBackgroundContent(getResources().getDrawable(R.drawable.messeger_like, null), -1);
        } else {
          sendButton.setBackgroundContent(getResources().getDrawable(R.drawable.comment_active_send, null), -1);
        }
      }
    });
    chatModel.observe(getViewLifecycleOwner(), new Observer<ChatSessionModel>() {
      @Override
      public void onChanged(ChatSessionModel model) {
        initSeenAction(model);
      }
    });
    viewModel.getImageSelected().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
      @Override
      public void onChanged(List<String> images) {
        int cnt = images.size();
        if (cnt == 0) {
          sendImageButtonPopup.dismiss();
        } else {
          sendImageButtonPopup.showAtLocation(imageSelectorFragment.getView(), Gravity.BOTTOM, 0, 0);
          sendImageButton.setTextContent("Send " + cnt);
        }
      }
    });

  }

  private Bundle vcc;

  private void initSeenAction(ChatSessionModel chatSessionModel) {
    LiveData<Bundle> lastMessage = chatSessionModel.getOnlineChat().getLastMessage();
    ChatSessionHandler chatHandler = chatSessionModel.getChatHandler();
    lastMessage.observe(getViewLifecycleOwner(), new Observer<Bundle>() {
      @Override
      public void onChanged(Bundle bundle) {
        if (vcc == bundle) return;
        vcc = bundle;
        chatHandler.onMeSeenMessage(bundle.getLong("time"));
      }
    });
  }

  private void initOnclick() {
    selectImage.setOnClickListener(view -> {
      if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
              != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                69);
      }
      imageSelectorFragment.show(getChildFragmentManager(), "image chooser");
//         pickImage.launch("image/*");
//         viewModel.getMessageContent().setValue("");
    });
    sendImageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        List<String> images = viewModel.getImageSelected().getValue();
        for (String uri : images) {
          Bundle data = new Bundle();
          data.putString("type", "image");
          data.putString("uri", uri);
          sendAction.apply(data);
        }
        imageSelectorFragment.dismiss();
      }
    });
    sendButton.setOnClickListener(view -> {

      String content = viewModel.getMsgContent().getValue();

      Bundle data = new Bundle();
      if (content == null || content.isEmpty()) {
        data.putString("type", "icon");
      } else {
        data.putString("type", "text");
        data.putString("content", content);
      }
      sendAction.apply(data);
      messsageEditText.setText("");
    });

    backButton.setOnClickListener(view -> getActivity().onBackPressed());
  }

  public void onDialogOpened() {
    int h = getResources().getDisplayMetrics().heightPixels;
    root.getLayoutParams().height = 3 * h / 5;
    root.requestLayout();
  }

  public void onDialogDismiss() {
    root.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
    root.requestLayout();
  }
}
