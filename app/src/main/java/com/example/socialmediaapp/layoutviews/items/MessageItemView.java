package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.AvatarView;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.home.fragment.main.MessageFragment;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.messenger.ImageMessageItem;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.messenger.TextMessageItem;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class MessageItemView extends ClickablePanel {
   private boolean fixShowTime;
   private boolean timeShown;
   private TextView timeTextView;
   private AvatarView seenView;
   private LifecycleOwner lifecycleOwner;
   private ViewGroup msgItemView;

   public MessageItemView(MessageFragment owner) {
      super(owner.getContext());
      this.lifecycleOwner = owner.getViewLifecycleOwner();

      timeShown = false;
      init(null);
      setRequestPressPaint(false);

      LayoutInflater inflater = LayoutInflater.from(getContext());
      msgItemView = (ViewGroup) inflater.inflate(R.layout.message_item, this, false);
      initOnClick();
   }

   public void setFixShowTime(boolean fixShowTime) {
      this.fixShowTime = fixShowTime;
      if (fixShowTime) {
         timeTextView.setVisibility(VISIBLE);
      } else {
         timeTextView.setVisibility(GONE);
      }
   }

   public void initViewModel(ChatSessionModel chatSessionModel, MessageItem messageItem) {
      View view;
      LayoutInflater inflater = LayoutInflater.from(getContext());

      if (messageItem instanceof TextMessageItem) {
         TextMessageItem textMessageItem = (TextMessageItem) messageItem;

         view = inflater.inflate(R.layout.text_message_item, msgItemView, false);
         TextView textView = view.findViewById(R.id.text_content);
         textView.setText(textMessageItem.getText());
      } else {
         ImageMessageItem imageMessageItem = (ImageMessageItem) messageItem;

         view = inflater.inflate(R.layout.image_message_item, msgItemView, false);
         ImageView imageView = view.findViewById(R.id.image_content);
         imageView.setImageDrawable(new BitmapDrawable(getResources(), imageMessageItem.getBitmap()));
      }

      timeTextView = view.findViewById(R.id.cnt_time);
      seenView = view.findViewById(R.id.seen_view);
      long time = messageItem.getTime();
      timeTextView.setText(parseTime(time));

      LiveData<UserBasicInfo> userLiveData = chatSessionModel.getUser();
      userLiveData.observe(lifecycleOwner, new Observer<UserBasicInfo>() {
         @Override
         public void onChanged(UserBasicInfo userBasicInfo) {
            seenView.setBackgroundContent(new BitmapDrawable(getResources(), userBasicInfo.getAvatar()), -1);
         }
      });
      LiveData<Long> lastSeen = chatSessionModel.getOnlineChat().getLastSeen();
      lastSeen.observe(lifecycleOwner, new Observer<Long>() {
         @Override
         public void onChanged(Long aLong) {
            if (aLong == messageItem.getTime()) {
               seenView.setVisibility(VISIBLE);
            } else {
               seenView.setVisibility(GONE);
               if (aLong > messageItem.getTime()) {
                  lastSeen.removeObserver(this);
               }
            }
         }
      });

   }

   private void initOnClick() {
      setOnClickListener(view -> {
         if (fixShowTime) return;
         if (timeShown) {
            timeTextView.setVisibility(GONE);
         } else {
            timeTextView.setVisibility(VISIBLE);
         }
         timeShown = !timeShown;
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
