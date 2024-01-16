package com.example.socialmediaapp.layoutviews.profile.model;

import android.graphics.Color;
import android.widget.LinearLayout;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;

public class FriendRequestProfileConfiguror extends Configuror {
  public FriendRequestProfileConfiguror(NotMeProfileView profileView) {
    super(profileView);
    handler = profileView.getViewModel().getHandler();
  }

  @Override
  protected void leftAction() {
    handler.accept();
  }

  @Override
  protected void rightAction() {
   handler.reject();
  }

  @Override
  public void configure() {
    leftButton.setTextContent("Accept");
    leftButton.setBackgroundColor(Color.parseColor("#0866FF"));
    leftButton.setTextContentColor(Color.WHITE);

    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) leftButton.getLayoutParams();
    params.weight = 1;
    leftButton.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.is_friend_white_24, null));
    rightButton.setTextContent("Reject");
    params = (LinearLayout.LayoutParams) rightButton.getLayoutParams();
    params.weight = 1;
    rightButton.setTextContentColor(Color.BLACK);
    rightButton.setBackgroundColor(Color.argb(15, 0, 0, 0));

    leftButton.requestLayout();
    rightButton.requestLayout();
  }
}
