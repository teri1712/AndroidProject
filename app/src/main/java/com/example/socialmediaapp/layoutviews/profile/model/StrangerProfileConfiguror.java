package com.example.socialmediaapp.layoutviews.profile.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.application.session.ProfileSessionHandler;
import com.example.socialmediaapp.models.messenger.chat.ChatInfo;
import com.example.socialmediaapp.view.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;

public class StrangerProfileConfiguror extends Configuror {
  public StrangerProfileConfiguror(NotMeProfileView profileView) {
    super(profileView);
  }

  @Override
  public void configure() {
    leftButton.setTextContent("Add friend");
    leftButton.setTextContentColor(Color.WHITE);
    leftButton.setBackgroundColor(Color.parseColor("#0866FF"));
    leftButton.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.add_friend_white_24, null));

    rightButton.setTextContent("Message");
    rightButton.setBackgroundColor(Color.argb(15, 0, 0, 0));
    rightButton.setTextContentColor(Color.BLACK);
    rightButton.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.messenger, null));
  }

  @Override
  public void leftAction() {
    handler.send();
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
