package com.example.socialmediaapp.layoutviews.profile.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.view.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;

public class FriendProfileConfiguror extends Configuror {
  public FriendProfileConfiguror(NotMeProfileView profileView) {
    super(profileView);
  }

  @Override
  public void configure() {
    leftButton.setTextContent("Friend");
    leftButton.setTextContentColor(Color.BLACK);
    leftButton.setBackgroundColor(Color.argb(15, 0, 0, 0));
    leftButton.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.is_friend_24, null));
    leftButton.setOnClickListener(null);
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) leftButton.getLayoutParams();
    params.weight = 45;

    rightButton.setTextContent("Message");
    rightButton.setBackgroundColor(Color.parseColor("#0866FF"));
    rightButton.setTextContentColor(Color.WHITE);
    rightButton.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.messenger_white, null));


    params = (LinearLayout.LayoutParams) rightButton.getLayoutParams();
    params.weight = 55;

    rightButton.requestLayout();
    rightButton.requestLayout();
  }

  @Override
  public void leftAction() {

  }

  @Override
  public void rightAction() {
    ChatInfo chat = profileView.getViewModel().getChatInfo();
    Context context = profileView.getContext();
    Intent intent = new Intent(context, MessageHome.class);
    intent.putExtra("chat id", chat.getChatId());
    intent.putExtra("other", chat.getOther());
    intent.putExtra("me", chat.getOther());
    intent.putExtra("fullname", chat.getFullname());
    context.startActivity(intent);
  }
}
