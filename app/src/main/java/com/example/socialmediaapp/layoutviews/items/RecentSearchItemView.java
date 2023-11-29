package com.example.socialmediaapp.layoutviews.items;

import android.view.ViewGroup;

import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.main.RecentSearchFragment;
import com.example.socialmediaapp.viewmodel.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class RecentSearchItemView extends UserBasicInfoItemView {
    public RecentSearchItemView(SearchFragment owner, UserBasicInfo userBasicInfo) {
        super(owner, userBasicInfo);
        eraseButton.setVisibility(VISIBLE);
        eraseButton.setOnClickListener(view -> {
            RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner.getChildFragmentManager().findFragmentByTag("recent search");
            RecentSearchFragmentViewModel recentSearchFragmentViewModel = recentSearchFragment.getViewModel();
            ViewGroup parent = (ViewGroup) getParent();
            recentSearchFragmentViewModel.deleteRecentSearchItem(userBasicInfo.getAlias(), parent.indexOfChild(RecentSearchItemView.this));
        });
    }

    @Override
    protected void actionOfOnClick() {
        RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner.getChildFragmentManager().findFragmentByTag("recent search");
        RecentSearchFragmentViewModel recentSearchFragmentViewModel = recentSearchFragment.getViewModel();
        ViewGroup parent = (ViewGroup) getParent();
        recentSearchFragmentViewModel.deleteRecentSearchItem(userBasicInfo.getAlias(), parent.indexOfChild(RecentSearchItemView.this));
        super.actionOfOnClick();
    }
}
