package com.example.socialmediaapp.layoutviews.items;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.AvatarView;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.main.RecentSearchFragment;
import com.example.socialmediaapp.viewmodel.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class UserBasicInfoItemView extends ClickablePanel {
    protected UserBasicInfo userBasicInfo;
    private View root;
    private AvatarView avatarView;
    protected CircleButton eraseButton;
    private TextView fullname, alias;
    protected HomePage homePage;
    protected LifecycleOwner lifecycleOwner;
    protected SearchFragment owner;

    public UserBasicInfoItemView(SearchFragment owner, UserBasicInfo userBasicInfo) {
        super(owner.getContext());
        this.owner = owner;
        lifecycleOwner = owner.getViewLifecycleOwner();
        this.userBasicInfo = userBasicInfo;
        homePage = (HomePage) owner.getActivity();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        root = inflater.inflate(R.layout.user_basic_info_item, this, false);
        addView(root);
        fullname = root.findViewById(R.id.fullname);
        eraseButton = root.findViewById(R.id.erase_button);
        avatarView = root.findViewById(R.id.avatar);
        alias = root.findViewById(R.id.alias);

        avatarView.setBackgroundContent(new BitmapDrawable(getResources(), userBasicInfo.getAvatar()), 0);
        fullname.setText(userBasicInfo.getFullname());
        alias.setText(userBasicInfo.getAlias());
        setOnClickListener(view -> {
            homePage.openViewProfileFragment(userBasicInfo);
            actionOfOnClick();
        });
    }

    protected void actionOfOnClick() {
        RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner.getChildFragmentManager().findFragmentByTag("recent search");
        RecentSearchFragmentViewModel recentSearchFragmentViewModel = recentSearchFragment.getViewModel();
        recentSearchFragmentViewModel.onClickToUserProfile(userBasicInfo.getAlias());
    }

}
