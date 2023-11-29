package com.example.socialmediaapp.layoutviews.profile.base;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.telephony.ims.ImsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.home.fragment.ViewProfileFragment;
import com.example.socialmediaapp.viewmodel.ViewProfileViewModel;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

public class ProfileView extends FrameLayout {
    protected View root;

    protected CircleButton avatar_button;
    protected ImageView background;
    protected TextView birthday, gender, fullname;
    protected Fragment owner;
    protected ViewProfileViewModel viewProfileViewModel;
    protected LifecycleOwner lifecycleOwner;

    public LifecycleOwner getLifecycleOwner() {
        return lifecycleOwner;
    }

    protected void init() {
        viewProfileViewModel.getLiveData().observe(lifecycleOwner, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                ImagePost avatarPost = userProfile.getAvatarPost();
                if (avatarPost != null) {
                    avatar_button.setBackgroundContent(new BitmapDrawable(getContext().getResources(), avatarPost.getImage()), 0);
                    viewProfileViewModel.getViewAvatarSessionId().observe(owner.getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            HomePage homePage = (HomePage) getContext();
                            Bundle args = new Bundle();
                            args.putInt("session id", integer);
                            homePage.openViewImageFragment(args, null, avatarPost.getImage());
                        }
                    });
                }
                ImagePost backgroundPost = userProfile.getBackgroundPost();
                if (backgroundPost != null) {
                    avatar_button.setBackgroundContent(new BitmapDrawable(getContext().getResources(), backgroundPost.getImage()), 0);
                    viewProfileViewModel.getViewBackgroundSessionId().observe(owner.getViewLifecycleOwner(), new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            HomePage homePage = (HomePage) getContext();
                            Bundle args = new Bundle();
                            args.putInt("session id", integer);
                            homePage.openViewImageFragment(args, null, avatarPost.getImage());
                        }
                    });
                }
                birthday.setText(userProfile.getBirthday());
                gender.setText(userProfile.getGender());
                fullname.setText(userProfile.getFullname());
            }
        });
    }

    protected void initOnClick() {
    }

    public ProfileView(@NonNull Fragment owner, int resource) {
        super(owner.getContext());
        this.owner = owner;
        lifecycleOwner = owner.getViewLifecycleOwner();
        viewProfileViewModel = ((ViewProfileFragment) owner).getViewModel();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        root = inflater.inflate(resource, this, true);
        background = (ImageView) root.findViewById(R.id.background);
        avatar_button = (CircleButton) root.findViewById(R.id.avatar_button);
        birthday = (TextView) root.findViewById(R.id.birthday_textview);
        gender = (TextView) root.findViewById(R.id.gender_textview);
        fullname = (TextView) root.findViewById(R.id.fullname);
        init();
        initOnClick();
    }
}
