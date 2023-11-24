package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.customview.AvatarView;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.viewmodels.HomePageViewModel;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;

public class UserBasicInfoItem extends ClickablePanel {
    private UserBasicInfo userBasicInfo;
    private View root;
    private AvatarView avatarButton;
    private CircleButton eraseButton;
    private TextView fullname, alias;
    private HomePage homePage;
    private String type;

    public UserBasicInfoItem(Context context, UserBasicInfo userBasicInfo, String type) {
        super(context);
        this.type = type;
        this.userBasicInfo = userBasicInfo;
        homePage = (HomePage) context;
        LayoutInflater inflater = LayoutInflater.from(context);
        root = (ViewGroup) inflater.inflate(R.layout.user_basic_info_item, this, false);
        addView(root);
        fullname = root.findViewById(R.id.fullname);
        eraseButton = root.findViewById(R.id.erase_button);
        avatarButton = root.findViewById(R.id.avatar);
        alias = root.findViewById(R.id.alias);
        avatarButton.setBackgroundContent(userBasicInfo.getAvatar(), 0);
        fullname.setText(userBasicInfo.getFullname());
        alias.setText(userBasicInfo.getAlias());
        initOnClick();
    }

    private void initOnClick() {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                homePage.openViewProfileFragment(userBasicInfo);
                homePage.getViewModel().onClickToUserProfile(getContext(), userBasicInfo);
            }
        });
        eraseButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("search")) {
                    ViewGroup parent = (ViewGroup) getParent();
                    parent.removeView(UserBasicInfoItem.this);
                } else {
                    homePage.getViewModel().removeRecentProfileItem(getContext(), userBasicInfo);
                }
            }
        });
    }
}
