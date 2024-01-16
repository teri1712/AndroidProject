package com.example.socialmediaapp.layoutviews.items.message;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.utils.TimeParserUtil;
import com.example.socialmediaapp.view.AvatarView;
import com.example.socialmediaapp.view.button.UserActiveView;
import com.example.socialmediaapp.view.container.ClickablePanel;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.chat.OnlineChat;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class ChatItemView extends ClickablePanel {
   private View root;
   private TextView fullname, messageContent, countTime;
   private UserActiveView userAvatar;
   private AvatarView seenView;
   private LifecycleOwner lifecycleOwner;
   private Drawable avatar;
   private Observer<Long> timeObserver;
   private LiveData<Long> timeCounter;

   public ChatItemView(@NonNull Context context) {
      super(context);
      init(null);
      setPressColor(Color.parseColor("#1fffffff"));
      MessageHome messageHome = (MessageHome) getContext();
      lifecycleOwner = messageHome;
      LayoutInflater inflater = LayoutInflater.from(getContext());
      root = inflater.inflate(R.layout.item_chat, this, false);
      addView(root);
      fullname = root.findViewById(R.id.fullname);
      messageContent = root.findViewById(R.id.message_text_view);
      seenView = root.findViewById(R.id.seen_state_view);
      userAvatar = root.findViewById(R.id.avatar_view);
      countTime = root.findViewById(R.id.cnt_time);
   }

   private void initValue(UserBasicInfoModel userBasicInfoModel) {
      if (userBasicInfoModel.getAvatar() != null) {
         avatar = new BitmapDrawable(getResources(), userBasicInfoModel.getAvatar());
      } else {
         avatar = getResources().getDrawable(R.drawable.avatar, null);
      }
      userAvatar.setBackgroundContent(avatar, -1);
      fullname.setText(userBasicInfoModel.getFullname());
   }

   public void initViewModel(ChatSessionModel chatSessionModel) {
      LiveData<UserBasicInfoModel> user = chatSessionModel.getOther();
      user.observe(lifecycleOwner, userBasicInfo -> initValue(userBasicInfo));

      OnlineChat onlineChat = chatSessionModel.getOnlineChat();

      LiveData<Bundle> lastMessage = onlineChat.getLastMessage();
      LiveData<Long> lastSeen = onlineChat.getLastSeen();
      LiveData<Long> meLastSeen = onlineChat.getMeLastSeen();
      LiveData<Boolean> isActive = onlineChat.getIsActive();


      lastMessage.observe(lifecycleOwner, new Observer<Bundle>() {
         @Override
         public void onChanged(Bundle bundle) {
            String msg = bundle.getString("view content");
            messageContent.setText(msg);
            long time = bundle.getLong("time");
            countTime.setText(TimeParserUtil.parseTime(time));
            String sender = bundle.getString("sender");
            if (sender.equals("You")) {
               seenView.setVisibility(VISIBLE);
               lastMessageIsMineChanged(time, lastSeen.getValue());
            } else {
               seenView.setVisibility(GONE);
               lastMessageIsMineChanged(time, lastSeen.getValue());
            }
         }
      });
      lastSeen.observe(lifecycleOwner, new Observer<Long>() {
         @Override
         public void onChanged(Long aLong) {
            long lastMsgTime = lastMessage.getValue().getLong("time");
            String sender = lastMessage.getValue().getString("sender");
            if (!sender.equals("You")) return;
            lastMessageIsMineChanged(lastMsgTime, aLong);
         }
      });
      meLastSeen.observe(lifecycleOwner, new Observer<Long>() {
         @Override
         public void onChanged(Long aLong) {
            long lastMsgTime = lastMessage.getValue().getLong("time");
            String sender = lastMessage.getValue().getString("sender");
            if (sender.equals("You")) return;
            lastMessageIsOthersChanged(lastMsgTime, aLong);
         }
      });

      timeObserver = aLong -> {
         int m = (int) ((aLong - onlineChat.getOffTime()) / (60 * 1000));
         if (m >= 60) {
            userAvatar.setUserState(UserActiveView.INACTIVE);
         } else if (m >= 1) {
            userAvatar.setUserState(new UserActiveView.UserTimeActive(m, getContext()));
         }
      };
      timeCounter = ((MessageHome) getContext()).getTimeCounter();

      isActive.observe(lifecycleOwner, aBoolean -> {
         if (aBoolean) {
            userAvatar.setUserState(UserActiveView.ACTIVE);
            timeCounter.removeObserver(timeObserver);
         } else {
            timeCounter.observe(lifecycleOwner, timeObserver);
         }
      });

      setOnClickListener(view -> {
         MessageHome messageHome = (MessageHome) getContext();
         messageHome.openChatFragment(chatSessionModel.getChatInfo());
      });
   }

   private void lastMessageIsMineChanged(Long lastMessageTime, Long lastSeen) {
      fullname.setTextColor(Color.parseColor("#ced4da"));
      countTime.setTextColor(Color.parseColor("#ced4da"));
      messageContent.setTextColor(Color.parseColor("#ced4da"));
      fullname.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
      countTime.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
      messageContent.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
      if (lastSeen == null || lastSeen < lastMessageTime) {
         seenView.setBackgroundContent(getResources().getDrawable(R.drawable.check_circle_filled_24, null), -1);
      } else {
         seenView.setBackgroundContent(avatar, -1);
      }
   }

   private void lastMessageIsOthersChanged(Long lastMessageTime, Long lastSeen) {
      if (lastSeen == null || lastSeen < lastMessageTime) {
         fullname.setTextColor(Color.WHITE);
         countTime.setTextColor(Color.WHITE);
         messageContent.setTextColor(Color.WHITE);
         fullname.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
         countTime.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
         messageContent.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_medium));
      } else {
         fullname.setTextColor(Color.parseColor("#ced4da"));
         countTime.setTextColor(Color.parseColor("#ced4da"));
         messageContent.setTextColor(Color.parseColor("#ced4da"));
         fullname.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
         countTime.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
         messageContent.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
      }

   }
}
