package com.example.socialmediaapp.layoutviews.items;

import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.RecentSearchFragment;
import com.example.socialmediaapp.viewmodel.fragment.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.application.repo.core.RecentSearchRepository;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class RecentSearchItemView extends UserBasicInfoItemView {
    public RecentSearchItemView(SearchFragment owner, UserBasicInfoModel userBasicInfoModel) {
        super(owner, userBasicInfoModel);
        eraseButton.setVisibility(VISIBLE);
        eraseButton.setOnClickListener(view -> {
            RecentSearchItemView.this.setVisibility(GONE);
            RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner
                    .getChildFragmentManager()
                    .findFragmentByTag("recent search");
            RecentSearchFragmentViewModel recentSearchFragmentViewModel = recentSearchFragment.getViewModel();
            String id = userBasicInfoModel.getId();

            RecentSearchRepository repository = recentSearchFragmentViewModel.getItemRepository();
            repository.deleteItem(id).observe(lifecycleOwner, s -> {
                if (!s.equals("Success")) {
                    RecentSearchItemView.this.setVisibility(VISIBLE);
                }
            });
        });
    }
}
