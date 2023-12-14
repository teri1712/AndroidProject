package com.example.socialmediaapp.activitiy;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.MessageSessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.main.MainMessageFragment;
import com.example.socialmediaapp.viewmodel.MessageHomeViewModel;
import com.example.socialmediaapp.viewmodel.adapter.ChatAdapter;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


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
   private ViewGroup onlineUserPanel;
   private RecyclerView recyclerView;
   private MessageHomeViewModel viewModel;

   private MessageSessionHandler messageSessionHandler;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      messageSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler.getMessageSessionHandler();
      super.onCreate(savedInstanceState);
      setContentView(R.layout.message_home);
      searchEditText = findViewById(R.id.search_edit_text);
      settingButton = findViewById(R.id.setting_button);
      editButton = findViewById(R.id.edit_button);
      onlineUserPanel = findViewById(R.id.online_user_panel);
      recyclerView = findViewById(R.id.chat_box_panel);

      initViewModel(messageSessionHandler);
      initRecyclerView();

   }

   private void initViewModel(MessageSessionHandler messageSessionHandler) {
      viewModel = new MessageHomeViewModel(messageSessionHandler);
   }

   private void initRecyclerView() {
      List<RecyclerViewExtra> viewExtras = new ArrayList<>();
      viewExtras.add(new SpinLoadViewExtra());
      ChatAdapter adapter = new ChatAdapter(this, new ArrayList<>(), viewExtras);
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

      LiveData<Update> chatListUpdate = viewModel.getChatListUpdate();
      chatListUpdate.observe(this, update -> {
         Update.Op op = update.op;
         HashMap<String, Object> data = update.data;
         int offset = (int) data.get("offset");
         if (op == Update.Op.ADD) {
            int length = (int) data.get("length");
            adapter.notifyItemRangeInserted(offset, length);
         } else if (op == Update.Op.REMOVE) {
            adapter.notifyItemRemoved(offset);
         }
      });
   }

   public MessageHomeViewModel getViewModel() {
      return viewModel;
   }

   @Override
   protected void onStart() {
      super.onStart();
      messageSessionHandler.setOnForeground(true);
   }

   @Override
   protected void onStop() {
      super.onStop();
      messageSessionHandler.setOnForeground(false);
   }

   public void openChatFragment(Integer chatSessionId) {
      FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
      String tag = "chat id" + chatSessionId;
      fragmentTransaction.add(R.id.fragment_container, MainMessageFragment.newInstance(chatSessionId), tag);
      fragmentTransaction.addToBackStack(tag);
      fragmentTransaction.commit();
   }

}