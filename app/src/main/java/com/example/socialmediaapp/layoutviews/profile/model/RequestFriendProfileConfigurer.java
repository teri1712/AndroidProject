package com.example.socialmediaapp.layoutviews.profile.model;

import android.graphics.Color;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.services.ServiceApi;

public class RequestFriendProfileConfigurer extends Configurer {
    private NotMeProfileView profileView;

    private RoundedButton left, right;

    public RequestFriendProfileConfigurer(NotMeProfileView profileView) {
        super(profileView.getContext());
        this.profileView = profileView;
        right = profileView.getBlueButton();
        left = profileView.getGreyButton();
    }

    @Override
    public void configure() {
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
        Configurer newCommand = new StrangerProfileConfigurer(profileView);
        profileView.changeConfiguration(newCommand);
        MutableLiveData<String> res = new MutableLiveData<>();
        res.observe(profileView.getOwner().getViewLifecycleOwner(), new Observer<String>() {
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
        ServiceApi.cancelFriendRequest(profileView.getProfile().getAlias(), res);
    }

    @Override
    public void rightAction() {

    }
}
