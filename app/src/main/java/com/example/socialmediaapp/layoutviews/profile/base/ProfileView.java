package com.example.socialmediaapp.layoutviews.profile.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.user.profile.base.UserProfile;

public class ProfileView extends FrameLayout {
    protected View root;

    protected CircleButton avatar_button;
    protected ImageView background;
    protected TextView birthday, gender, fullname;
    protected Fragment owner;
    protected UserProfile profile;
    protected HomePage activity;

    public Fragment getOwner() {
        return owner;
    }

    protected void init() {
        ImagePost avatarPost = profile.getAvatarPost();
        if (avatarPost != null) {
            avatar_button.setBackgroundContent(profile.getAvatarPost().getImage(), 0);
        }
        ImagePost backgroundPost = profile.getBackgroundPost();
        if (backgroundPost != null) {
            background.setClickable(true);
            background.setImageDrawable(backgroundPost.getImage());
        }
        if (profile.getBirthday() != null) {
            birthday.setText(profile.getBirthday().toString());
        }
        if (profile.getGender() != null) {
            gender.setText(profile.getGender());
        }
        fullname.setText(profile.getFullname());
    }

    protected void initOnClick() {
        ImagePost avatarPost = profile.getAvatarPost();
        if (avatarPost != null) {
            avatar_button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomePage activity = (HomePage) owner.getActivity();
                    activity.openViewImageFragment(null, avatarPost);
                }
            });
        }
        ImagePost backgroundPost = profile.getBackgroundPost();
        if (backgroundPost != null) {
            background.setClickable(true);
            background.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomePage activity = (HomePage) owner.getActivity();
                    activity.openViewImageFragment(null, backgroundPost);
                }
            });
        }
    }

    public ProfileView(@NonNull Context context, UserProfile profile, Fragment owner, int resource) {
        super(context);
        activity = (HomePage) owner.getActivity();
        this.profile = profile;
        this.owner = owner;
        LayoutInflater inflater = LayoutInflater.from(context);
        root = inflater.inflate(resource, this, true);
        background = (ImageView) root.findViewById(R.id.background);
        avatar_button = (CircleButton) root.findViewById(R.id.avatar_button);
        birthday = (TextView) root.findViewById(R.id.birthday_textview);
        gender = (TextView) root.findViewById(R.id.gender_textview);
        fullname = (TextView) root.findViewById(R.id.fullname);
        init();
        initOnClick();
    }

    public UserProfile getProfile() {
        return profile;
    }
}
