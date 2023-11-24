package com.example.socialmediaapp.layoutviews.profile.model;

import android.graphics.Color;
import android.widget.LinearLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.services.ServiceApi;

public class FriendRequestProfileConfigurer extends Configurer {
    private NotMeProfileView profileView;
    private RoundedButton left, right;

    public FriendRequestProfileConfigurer(NotMeProfileView profileView) {
        super(profileView.getContext());
        this.profileView = profileView;
        right = profileView.getBlueButton();
        left = profileView.getGreyButton();
    }

    @Override
    protected void leftAction() {
        final Configurer newCommand = new FriendProfileConfigurer(profileView);
        profileView.changeConfiguration(newCommand);

        MutableLiveData<String> res = new MutableLiveData<>();
        res.observe(profileView.getOwner().getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    newCommand.allowActionLeft();
                    newCommand.allowActionRight();
                } else {
                    profileView.changeConfiguration(FriendRequestProfileConfigurer.this);
                }
            }
        });
        ServiceApi.acceptFriendRequest(profileView.getProfile().getAlias(), res);
    }

    @Override
    protected void rightAction() {
        final Configurer newCommand = new StrangerProfileConfigurer(profileView);
        profileView.changeConfiguration(newCommand);

        MutableLiveData<String> res = new MutableLiveData<>();
        res.observe(profileView.getOwner().getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    newCommand.allowActionLeft();
                    newCommand.allowActionRight();
                } else {
                    profileView.changeConfiguration(FriendRequestProfileConfigurer.this);
                }
            }
        });
        ServiceApi.rejectFriendRequest(profileView.getProfile().getAlias(), res);
    }

    @Override
    public void configure() {
        left.setTextContent("Accept");
        left.setBackgroundColor(Color.parseColor("#0866FF"));
        left.setTextContentColor(Color.WHITE);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) left.getLayoutParams();
        params.weight = 1;
        left.setBackgroundContent(profileView.getResources().getDrawable(R.drawable.is_friend_white_24, null));
        right.setTextContent("Reject");
        params = (LinearLayout.LayoutParams) right.getLayoutParams();
        params.weight = 1;
        right.setTextContentColor(Color.BLACK);
        right.setBackgroundColor(Color.argb(15, 0, 0, 0));

        left.requestLayout();
        right.requestLayout();
    }
}
