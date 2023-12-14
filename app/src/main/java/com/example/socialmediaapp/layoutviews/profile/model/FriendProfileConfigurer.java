package com.example.socialmediaapp.layoutviews.profile.model;

import android.graphics.Color;
import android.widget.LinearLayout;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;

public class FriendProfileConfigurer extends Configurer {
    private NotMeProfileView profileView;
    private RoundedButton left, right;

    public FriendProfileConfigurer(NotMeProfileView profileView, ViewProfileSessionHandler handler) {
        super(profileView.getContext(), handler);
        this.profileView = profileView;
    }

    @Override
    public void configure() {
        right = profileView.getGreyButton();
        left = profileView.getBlueButton();
        left.setTextContent("Friend");
        left.setTextContentColor(Color.BLACK);
        left.setBackgroundColor(Color.argb(15, 0, 0, 0));
        left.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.is_friend_24, null));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) left.getLayoutParams();
        params.weight = 45;

        right.setTextContent("Message");
        right.setBackgroundColor(Color.parseColor("#0866FF"));
        right.setTextContentColor(Color.WHITE);
        right.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.messenger_white, null));

        params = (LinearLayout.LayoutParams) right.getLayoutParams();
        params.weight = 55;

        left.requestLayout();
        right.requestLayout();
    }

    @Override
    public void leftAction() {

    }

    @Override
    public void rightAction() {

    }
}
