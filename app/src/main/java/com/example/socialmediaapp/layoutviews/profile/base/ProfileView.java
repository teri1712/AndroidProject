package com.example.socialmediaapp.layoutviews.profile.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
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

    public void initViewModel() {
        LiveData<UserProfile> userProfile = viewProfileViewModel.getLiveData();
        userProfile.observe(lifecycleOwner, new Observer<UserProfile>() {
            @Override
            public void onChanged(UserProfile userProfile) {
                birthday.setText(userProfile.getBirthday());
                gender.setText(userProfile.getGender());
                fullname.setText(userProfile.getFullname());
            }
        });
        LiveData<SessionHandler> viewAvatarPostSession = viewProfileViewModel.getViewAvatarSession();
        viewAvatarPostSession.observe(lifecycleOwner, new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                PostSessionHandler avatarPostSession = (PostSessionHandler) sessionHandler;
                Integer handlerId = sessionHandler.getId();
                avatar_button.setOnClickListener(view -> {
                    HomePage homePage = (HomePage) getContext();
                    Bundle args = new Bundle();
                    args.putInt("session id", handlerId);
                    ImagePost avatarPost = (ImagePost) avatarPostSession.getDataSyncEmitter().getValue();
                    homePage.openViewImageFragment(args, null, avatarPost.getImage());
                });
                avatarPostSession.getDataSyncEmitter().observe(lifecycleOwner, post -> {
                    Bitmap bitmap = ((ImagePost) post).getImage();
                    avatar_button.setBackgroundContent(new BitmapDrawable(getContext().getResources(), bitmap), 0);
                });
            }
        });


        LiveData<SessionHandler> viewBackgroundPostSession = viewProfileViewModel.getViewBackgroundSession();
        viewBackgroundPostSession.observe(lifecycleOwner, new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                PostSessionHandler backgroundPostSession = (PostSessionHandler) sessionHandler;

                Integer handlerId = sessionHandler.getId();
                background.setOnClickListener(view -> {
                    HomePage homePage = (HomePage) getContext();
                    Bundle args = new Bundle();
                    args.putInt("session id", handlerId);
                    ImagePost backgroundPost = (ImagePost) backgroundPostSession.getDataSyncEmitter().getValue();
                    homePage.openViewImageFragment(args, null, backgroundPost.getImage());
                });
                backgroundPostSession.getDataSyncEmitter().observe(lifecycleOwner, post -> {
                    Bitmap bitmap = ((ImagePost) post).getImage();
                    background.setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
                });
            }
        });
        initOnClick();
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
        background = root.findViewById(R.id.background);
        avatar_button = root.findViewById(R.id.avatar_button);
        birthday = root.findViewById(R.id.birthday_textview);
        gender = root.findViewById(R.id.gender_textview);
        fullname = root.findViewById(R.id.fullname);
    }
}
