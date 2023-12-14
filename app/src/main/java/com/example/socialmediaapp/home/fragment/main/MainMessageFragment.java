package com.example.socialmediaapp.home.fragment.main;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.MessageSessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.UserActiveView;
import com.example.socialmediaapp.customview.progress.SendCommentloading;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.layoutviews.items.SendingMessageView;
import com.example.socialmediaapp.viewmodel.MainMessageFragmentViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.messenger.MessageFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.messenger.OnlineChat;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MainMessageFragment extends Fragment {

   public class SendingMessageViewExtra extends RecyclerViewExtra {
      private SendingMessageView sendMessageloading;

      public SendingMessageViewExtra() {
         super(new SendCommentloading(getContext()), Position.END);
         sendMessageloading = (SendingMessageView) view;
      }

      @Override
      public void configure(View view) {
         MutableLiveData<String> sending = viewModel.getSendMessageState();

         sending.observe(getViewLifecycleOwner(), s -> {
            if (s.equals("Idle")) {
               finishSending();
            } else if (s.equals("Sending")) {
               performSending();
            } else {
               sending.setValue("Idle");
            }
         });

         sending.observe(getViewLifecycleOwner(), s -> {
            if (s.equals("Sending")) {
               Uri imageUri = viewModel.getImage().getValue();
               String content = viewModel.getMessageContent().getValue();
               if (imageUri == null) {
                  sendMessageloading.setTextContent(content);
               } else {
                  sendMessageloading.setImageContent(imageUri);
               }
            }
         });
      }

      private void performSending() {
         sendMessageloading.setVisibility(View.VISIBLE);
      }

      private void finishSending() {
         sendMessageloading.setVisibility(View.GONE);
      }
   }

   public class SendActionConfigurator implements MessageFragment.ConfigureExtra {
      @Override
      public void apply(View root, MessageFragmentViewModel messageFragmentViewModel) {
         sendAction = data -> {
            MediatorLiveData<String> sendCommentState = viewModel.getSendMessageState();
            sendCommentState.setValue("Sending");
            LiveData<String> callBack = messageFragmentViewModel.uploadMessage(data);

            callBack.observe(getViewLifecycleOwner(), s -> {
               if (!s.equals("Success")) {
                  Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
               }
               sendCommentState.setValue("Complete");
            });
            return callBack;
         };
         initOnclick();
      }
   }

   public class ScrollButtonConfigurator implements MessageFragment.ConfigureExtra {
      private RecyclerView recyclerView;
      private RecyclerView.Adapter adapter;
      private boolean onScreen;

      @Override
      public void apply(View root, MessageFragmentViewModel messageFragmentViewModel) {
         recyclerView = root.findViewById(R.id.message_panel);
         adapter = recyclerView.getAdapter();
         recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

               int pos = linearLayoutManager.findLastCompletelyVisibleItemPosition();
               int total = adapter.getItemCount();

               if (total >= pos + 4) {
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
         scrollDownButton.setOnClickListener(view -> recyclerView.scrollToPosition(adapter.getItemCount() - 1));
      }
   }

   public MainMessageFragment() {
   }

   public static MainMessageFragment newInstance(Integer chatId) {
      Bundle args = new Bundle();
      args.putInt("chat id", chatId);
      MainMessageFragment fragment = new MainMessageFragment();
      fragment.setArguments(args);
      return fragment;
   }

   private ActivityResultLauncher<String> pickImage;
   private Integer chatId;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Bundle args = getArguments();
      chatId = args.getInt("chat id");
      pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
         @Override
         public void onActivityResult(Uri uri) {
            if (uri == null) return;
            viewModel.getImage().setValue(uri);
            sendButton.performClick();
         }
      });
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

      initViewModel();

      MessageSessionHandler messageSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler.getMessageSessionHandler();
      MessageSessionHandler.ChatSessionModelStore chatSessionModelStore = messageSessionHandler.getChatSessionModelStore();
      LiveData<ChatSessionModel> chatSessionModelLiveData = chatSessionModelStore.findByChatId(chatId);

      initMessageFragment(chatSessionModelLiveData);

      chatSessionModelLiveData.observe(getViewLifecycleOwner(), chatSessionModel1 -> initOnclick());
      chatSessionModelLiveData.observe(getViewLifecycleOwner(), chatSessionModel -> initOnlineValues(chatSessionModel));
      return root;
   }

   private void initOnlineValues(ChatSessionModel chatSessionModel) {
      LiveData<UserBasicInfo> user = chatSessionModel.getUser();
      user.observe(getViewLifecycleOwner(), new Observer<UserBasicInfo>() {
         @Override
         public void onChanged(UserBasicInfo u) {
            fullname.setText(u.getFullname());
            avatarView.setBackgroundContent(new BitmapDrawable(getResources(), u.getAvatar()), -1);
         }
      });

      OnlineChat onlineChat = chatSessionModel.getOnlineChat();
      onlineChat.getIsActive().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
         @Override
         public void onChanged(Boolean aBoolean) {
            if (aBoolean) {
               avatarView.setUserState(UserActiveView.ACTIVE);
               onlineState.setText("online");
            } else {
               //clock count here
               avatarView.setUserState(UserActiveView.ACTIVE);
            }
         }
      });

   }

   private void initMessageFragment(LiveData<ChatSessionModel> chatSessionModelLiveData) {
      FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
      RecyclerViewExtra sendCommentView = new SendingMessageViewExtra();

      List<MessageFragment.ConfigureExtra> configureExtras = new ArrayList<>();
      configureExtras.add(new MessageFragment.ScrollConfigurator());
      configureExtras.add(new SendActionConfigurator());
      configureExtras.add(new ScrollButtonConfigurator());

      List<RecyclerViewExtra> extras = new ArrayList<>();
      extras.add(sendCommentView);

      MessageFragment messageFragment = new MessageFragment(chatSessionModelLiveData, configureExtras, extras);
      fragmentTransaction.replace(R.id.message_fragment_container, messageFragment);
      fragmentTransaction.commit();

   }

   private void initViewModel() {
      viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(MainMessageFragmentViewModel.class);
      viewModel.getMessageContent().observe(getViewLifecycleOwner(), new Observer<String>() {
         @Override
         public void onChanged(String s) {
            if (!Objects.equals(s, messsageEditText.getText().toString())) {
               messsageEditText.setText(s);
            }
            viewModel.getMessageContent().removeObserver(this);
         }
      });
      messsageEditText.addTextChangedListener(new TextWatcher() {
         @Override
         public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         }

         @Override
         public void afterTextChanged(Editable editable) {
            viewModel.getMessageContent().setValue(editable.toString());
         }
      });
      viewModel.getMessageContent().observe(getViewLifecycleOwner(), new Observer<String>() {
         @Override
         public void onChanged(String s) {
            if (s.isEmpty()) {
               sendButton.setBackgroundContent(getResources().getDrawable(R.drawable.messeger_like, null), -1);
            } else {
               sendButton.setBackgroundContent(getResources().getDrawable(R.drawable.comment_active_send, null), -1);
            }
         }
      });
   }

   private void initOnclick() {
      selectImage.setOnClickListener(view -> {
         pickImage.launch("image/*");
         viewModel.getMessageContent().setValue("");
      });

      sendButton.setOnClickListener(view -> {
         MutableLiveData<String> sendState = viewModel.getSendMessageState();
         if (sendState.getValue().equals("Sending")) {
            Toast.makeText(getContext(), "please wait", Toast.LENGTH_SHORT).show();
            return;
         }

         MutableLiveData<Uri> imageUri = viewModel.getImage();
         Uri uri = imageUri.getValue();
         MutableLiveData<String> content = viewModel.getMessageContent();

         Bundle data = new Bundle();
         data.putString("content", content.getValue());
         data.putString("image content", uri == null ? null : uri.toString());
         sendAction.apply(data);

         imageUri.setValue(null);
         messsageEditText.setText("");
      });
   }

}
