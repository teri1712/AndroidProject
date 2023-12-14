package com.example.socialmediaapp.layoutviews.profile.model;

import android.graphics.Color;

import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.ViewProfileSessionHandler;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;

public class RequestFriendProfileConfigurer extends Configurer {
    private NotMeProfileView profileView;

    private RoundedButton left, right;

    public RequestFriendProfileConfigurer(NotMeProfileView profileView, ViewProfileSessionHandler handler) {
        super(profileView.getContext(), handler);
        this.profileView = profileView;

    }

    @Override
    public void configure() {
        right = profileView.getGreyButton();
        left = profileView.getBlueButton();
        left.setTextContent("Cancel request");
        left.setBackgroundColor(Color.parseColor("#0866FF"));
        left.setTextContentColor(Color.WHITE);
        left.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.add_friend_white_24, null));

        right.setTextContentColor(Color.BLACK);
        right.setTextContent("Message");
        right.setBackgroundColor(Color.argb(15, 0, 0, 0));
        right.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.messenger, null));
    }

    @Override
    public void leftAction() {
        Configurer newCommand = new StrangerProfileConfigurer(profileView, handler);
        profileView.changeConfiguration(newCommand);
        handler.cancelFriendRequest().observe(profileView.getLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    newCommand.allowActionLeft();
                    newCommand.allowActionRight();
                } else {
                    profileView.changeConfiguration(RequestFriendProfileConfigurer.this);
                }
            }
        });
    }

    @Override
    public void rightAction() {

    }
}
