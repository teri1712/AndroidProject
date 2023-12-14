package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.MessageHome;
import com.example.socialmediaapp.customview.AvatarView;
import com.example.socialmediaapp.customview.button.UserActiveView;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.messenger.OnlineChat;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.Objects;

public class ChatItemView extends ClickablePanel {
   private View root;
   private TextView fullname, messageContent, countTime;
   private UserActiveView userAvatar;
   private AvatarView seenView;
   private LifecycleOwner lifecycleOwner;
   private Drawable avatar;

   public ChatItemView(@NonNull Context context) {
      super(context);
      MessageHome messageHome = (MessageHome) getContext();
      lifecycleOwner = messageHome;
      LayoutInflater inflater = LayoutInflater.from(getContext());
      root = inflater.inflate(R.layout.chat_box_item, this, false);
      fullname = root.findViewById(R.id.fullname);
      messageContent = root.findViewById(R.id.message_text_view);
      seenView = root.findViewById(R.id.seen_state_view);
      userAvatar = root.findViewById(R.id.avatar_view);
      countTime = root.findViewById(R.id.cnt_time);
   }

   private void initValue(UserBasicInfo userBasicInfo) {
      avatar = new BitmapDrawable(getResources(), userBasicInfo.getAvatar());
      userAvatar.setBackgroundContent(avatar, -1);
      fullname.setText(userBasicInfo.getFullname());
   }

   public void initViewModel(ChatSessionModel chatSessionModel) {
      LiveData<UserBasicInfo> user = chatSessionModel.getUser();
      user.observe(lifecycleOwner, userBasicInfo -> initValue(userBasicInfo));

      OnlineChat onlineChat = chatSessionModel.getOnlineChat();

      LiveData<Bundle> lastMessage = onlineChat.getLastMessage();
      LiveData<Long> lastSeen = onlineChat.getLastSeen();
      LiveData<Boolean> isActive = onlineChat.getIsActive();

      lastMessage.observe(lifecycleOwner, new Observer<Bundle>() {
         @Override
         public void onChanged(Bundle bundle) {
            String msg = bundle.getString("content");
            messageContent.setText(msg);
            long time = bundle.getLong("time");
            countTime.setText(parseTime(time));
            seenView.setBackgroundContent(getResources().getDrawable(R.drawable.check_circle_filled_24, null), -1);
            seenView.setBackgroundContent(avatar, -1);
         }
      });
      lastSeen.observe(lifecycleOwner, new Observer<Long>() {
         @Override
         public void onChanged(Long aLong) {
            long lastMsgTime = lastMessage.getValue().getLong("time");
            if (lastMsgTime <= aLong) {
               seenView.setBackgroundContent(avatar, -1);
            }
         }
      });
      isActive.observe(lifecycleOwner, new Observer<Boolean>() {
         @Override
         public void onChanged(Boolean aBoolean) {
            if (aBoolean) {
               userAvatar.setUserState(UserActiveView.ACTIVE);
            } else {
               // register clock count active state
               userAvatar.setUserState(UserActiveView.INACTIVE);
            }
         }
      });

      setOnClickListener(new OnClickListener() {
         @Override
         public void onClick(View view) {
            Integer chatId = chatSessionModel.getChatInfo().getChatId();
            MessageHome messageHome = (MessageHome) getContext();
            messageHome.openChatFragment(chatId);
         }
      });
   }

   private String parseTime(long time) {
      SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm");
      String timeDate = formatter.format(time);
      String oTime = timeDate.split(" ")[1];

      SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
      String df1 = dateFormatter.format(System.currentTimeMillis());
      String df2 = dateFormatter.format(time);
      String[] d1 = df1.split("-");
      String[] d2 = df2.split("-");
      if (!Objects.equals(d1[2], d2[2])) {
         return timeDate;
      }
      if (!Objects.equals(df1, df2)) {
         int month = Integer.parseInt(d2[1]);
         return d2[0] + " " + Month.of(month).getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + oTime;
      }

      return oTime;
   }
}
