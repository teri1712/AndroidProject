package com.example.socialmediaapp.layoutviews.profile.model;

import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.services.ServiceApi;

public class StrangerProfileConfigurer extends Configurer {
    private NotMeProfileView profileView;
    private RoundedButton left, right;

    public StrangerProfileConfigurer(NotMeProfileView profileView) {
        super(profileView.getContext());
        this.profileView = profileView;
        right = profileView.getBlueButton();
        left = profileView.getGreyButton();
    }

    @Override
    public void configure() {
        left.setTextContent("Add friend");
        left.setTextContentColor(Color.BLACK);
        left.setBackgroundColor(Color.argb(15, 0, 0, 0));
        left.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.add_friend_24, null));

        right.setTextContent("Message");
        right.setBackgroundColor(Color.parseColor("#0866FF"));
        right.setTextContentColor(Color.WHITE);
        right.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.messenger_white, null));
    }

    @Override
    public void leftAction() {
        Configurer newCommand = new RequestFriendProfileConfigurer(profileView);
        profileView.changeConfiguration(newCommand);
        MutableLiveData<String> res = new MutableLiveData<>();
        res.observe(profileView.getOwner().getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    newCommand.allowActionLeft();
                    newCommand.allowActionRight();
                } else {
                    profileView.changeConfiguration(StrangerProfileConfigurer.this);
                }
            }
        });
        ServiceApi.sendFriendRequest(profileView.getProfile().getAlias(), res);
    }

    @Override
    public void rightAction() {

    }
}
