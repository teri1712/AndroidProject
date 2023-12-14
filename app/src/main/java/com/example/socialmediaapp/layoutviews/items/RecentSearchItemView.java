package com.example.socialmediaapp.layoutviews.items;

import android.view.ViewGroup;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.main.RecentSearchFragment;
import com.example.socialmediaapp.viewmodel.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.repo.RecentSearchRepository;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class RecentSearchItemView extends UserBasicInfoItemView {
    public RecentSearchItemView(SearchFragment owner, UserBasicInfo userBasicInfo) {
        super(owner, userBasicInfo);
        eraseButton.setVisibility(VISIBLE);
        eraseButton.setOnClickListener(view -> {
            RecentSearchItemView.this.setVisibility(GONE);
            RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner.getChildFragmentManager().findFragmentByTag("recent search");
            RecentSearchFragmentViewModel recentSearchFragmentViewModel = recentSearchFragment.getViewModel();
            String alias = userBasicInfo.getAlias();

            RecentSearchRepository repository = recentSearchFragmentViewModel.getItemRepository();
            repository.deleteItem(alias).observe(lifecycleOwner, s -> {
                if (!s.equals("Success")) {
                    RecentSearchItemView.this.setVisibility(VISIBLE);
                }
            });
        });
    }

    @Override
    protected void actionOfOnClick() {
        RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner.getChildFragmentManager().findFragmentByTag("recent search");
        RecentSearchFragmentViewModel recentSearchFragmentViewModel = recentSearchFragment.getViewModel();
        LiveData<String> callBack = recentSearchFragmentViewModel.onClickToUserProfile(userBasicInfo.getAlias());
        callBack.observe(lifecycleOwner, s -> {
            if (s.equals("Success")) {
                ViewGroup parent = (ViewGroup) getParent();
                parent.removeView(RecentSearchItemView.this);
            } else {
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
