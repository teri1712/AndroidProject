package com.example.socialmediaapp.layoutviews.items;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.view.button.UserActiveView;
import com.example.socialmediaapp.models.messenger.OnlineUserItem;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class OnlineUserItemView extends FrameLayout {
   private View root;
   private UserActiveView userActiveView;
   private TextView fullname;
   private LifecycleOwner lifecycleOwner;
   private long offTime;
   private Observer<Long> timeObserver;
   private LiveData<Long> timeCounter;

   public OnlineUserItemView(MessageHome messageHome) {
      super(messageHome);
      lifecycleOwner = messageHome;

      LayoutInflater inflater = LayoutInflater.from(getContext());
      root = inflater.inflate(R.layout.item_user_state, this, false);
      addView(root);
      userActiveView = root.findViewById(R.id.user_active);
      fullname = root.findViewById(R.id.fullname);
   }

   private void initValue(UserBasicInfoModel userBasicInfoModel) {
      fullname.setText(userBasicInfoModel.getFullname());
      if (userBasicInfoModel.getAvatar() != null)
         userActiveView.setBackgroundContent(new BitmapDrawable(getResources(), userBasicInfoModel.getAvatar()), -1);
   }

   public void initViewModel(OnlineUserItem onlineUserItem) {
      initValue(onlineUserItem.getUserBasicInfo());
      timeObserver = aLong -> {
         int m = (int) ((aLong - offTime) / (60 * 1000));
         if (m >= 60) {
            setVisibility(GONE);
         } else if (m >= 1) {
            userActiveView.setUserState(new UserActiveView.UserTimeActive(m, getContext()));
         }
      };
      timeCounter = ((MessageHome) getContext()).getTimeCounter();

      onlineUserItem.getIsOnline().observe(lifecycleOwner, aBoolean -> {
         if (aBoolean) {
            setVisibility(VISIBLE);
            userActiveView.setUserState(UserActiveView.ACTIVE);
            timeCounter.removeObserver(timeObserver);
         } else {
            offTime = System.currentTimeMillis();
            timeCounter.observe(lifecycleOwner, timeObserver);
         }
      });

      root.setOnClickListener(view -> {
         MessageHome messageHome = (MessageHome) getContext();
         messageHome.openChatFragment(onlineUserItem.getChatInfo());
      });

   }
}
