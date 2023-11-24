package com.example.socialmediaapp.layoutviews.profile;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.base.ProfileView;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.user.profile.SelfProfile;

public class SelfProfileView extends ProfileView {
    private CircleButton selectAvatarButton, selectBackgroundButton;
    private RoundedButton editInformationButton;

    @Override
    protected void init() {
        MutableLiveData<SelfProfile> delegate = ((SelfProfile) profile).getDelegateToLocalLiveData();
        delegate.observe(owner.getViewLifecycleOwner(), new Observer<SelfProfile>() {
            @Override
            public void onChanged(SelfProfile selfProfile) {
                ImagePost avatarPost = selfProfile.getAvatarPost();
                if (avatarPost != null) {
                    avatar_button.setBackgroundContent(avatarPost.getImage(), 0);
                    avatar_button.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.openViewImageFragment(null, avatarPost);
                        }
                    });
                }
                ImagePost backgroundPost = selfProfile.getBackgroundPost();
                if (backgroundPost != null) {
                    background.setClickable(true);
                    background.setImageDrawable(backgroundPost.getImage());
                    background.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.openViewImageFragment(null, backgroundPost);
                        }
                    });
                }
                if (selfProfile.getBirthday() != null) {
                    birthday.setText(selfProfile.getBirthday().toString());
                }
                if (selfProfile.getGender() != null) {
                    gender.setText(selfProfile.getGender());
                }
                fullname.setText(selfProfile.getFullname());
            }
        });
    }

    @Override
    protected void initOnClick() {
        super.initOnClick();

        selectAvatarButton = (CircleButton) root.findViewById(R.id.select_avatar_button);
        selectBackgroundButton = (CircleButton) root.findViewById(R.id.select_background_button);
        selectAvatarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.requestUpdateAvatar();
            }
        });
        selectBackgroundButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.requestUpdateBackground();
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

    public SelfProfileView(@NonNull Context context, SelfProfile profile, Fragment owner) {
        super(context, profile, owner, R.layout.self_profile);
    }

}
