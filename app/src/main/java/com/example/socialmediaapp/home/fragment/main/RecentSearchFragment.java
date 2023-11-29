package com.example.socialmediaapp.home.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.layoutviews.items.RecentSearchItemView;
import com.example.socialmediaapp.viewmodel.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

public class RecentSearchFragment extends Fragment {

    public RecentSearchFragment(RecentSearchAccessHandler handler) {
        this.handler = handler;
    }

    private RecentSearchAccessHandler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new RecentSearchFragmentViewModel(handler);
    }

    private ViewGroup itemPanel;
    private RecentSearchFragmentViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_posts, container, false);

        itemPanel = view.findViewById(R.id.posts_panel);


        initViewModel();
        return view;
    }

    public RecentSearchFragmentViewModel getViewModel() {
        return viewModel;
    }

    public void initViewModel() {
        viewModel.getItemUpdate().observe(getViewLifecycleOwner(), new Observer<Update<UserBasicInfo>>() {
            @Override
            public void onChanged(Update<UserBasicInfo> itemUpdate) {
                Update.Op op = itemUpdate.getOp();
                int pos = itemUpdate.getPos();
                UserBasicInfo item = itemUpdate.getItem();
                if (op == Update.Op.ADD) {
                    RecentSearchItemView itemView = new RecentSearchItemView((SearchFragment) getParentFragment(), item);
                    if (pos == -1) {
                        itemPanel.addView(itemView);
                    } else {
                        itemPanel.addView(itemView, 0);
                    }
                } else {
                    itemPanel.removeViewAt(pos);
                }
            }
        });
        viewModel.loadItems();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

}
