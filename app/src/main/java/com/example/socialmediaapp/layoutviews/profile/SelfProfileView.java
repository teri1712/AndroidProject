package com.example.socialmediaapp.layoutviews.profile;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.base.ProfileView;
import com.example.socialmediaapp.viewmodel.models.user.profile.SelfProfile;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

public class SelfProfileView extends ProfileView {
    private CircleButton selectAvatarButton, selectBackgroundButton;
    private RoundedButton editInformationButton;

    @Override
    protected void initOnClick() {
        super.initOnClick();
        HomePage activity = (HomePage) getContext();
        selectAvatarButton = (CircleButton) root.findViewById(R.id.select_avatar_button);
        selectBackgroundButton = (CircleButton) root.findViewById(R.id.select_background_button);
        selectAvatarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openUpdateAvatarFragment();
            }
        });
        selectBackgroundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openUpdateBackgroundFragment();
            }
        });
        editInformationButton = root.findViewById(R.id.edit_information_button);
        editInformationButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openEditInformationFragment();
            }
        });
    }

    public SelfProfileView(@NonNull Fragment owner) {
        super(owner, R.layout.self_profile);
    }

}
