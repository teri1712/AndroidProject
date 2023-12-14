package com.example.socialmediaapp.home.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.layoutviews.items.RecentSearchItemView;
import com.example.socialmediaapp.viewmodel.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.util.HashMap;
import java.util.List;

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
    private CustomSpinningView loadSpinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recent_search, container, false);
        loadSpinner = view.findViewById(R.id.load_spinner);
        itemPanel = view.findViewById(R.id.posts_panel);


        initViewModel();
        return view;
    }

    public RecentSearchFragmentViewModel getViewModel() {
        return viewModel;
    }

    public void initViewModel() {
        viewModel.getLoadItemState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                loadSpinner.setVisibility(View.VISIBLE);
            } else {
                loadSpinner.setVisibility(View.GONE);
            }
        });
        Repository<UserBasicInfo> repository = viewModel.getItemRepository();
        LiveData<Update> itemUpdate = repository.getItemUpdate();
        itemUpdate.observe(getViewLifecycleOwner(), update -> {
            Update.Op op = update.op;
            HashMap<String, Object> data = update.data;
            int offset = (int) data.get("offset");
            if (op == Update.Op.ADD) {
                int length = (int) data.get("length");
                for (int i = 0; i < length; i++) {
                    UserBasicInfo item = repository.get(offset + i);
                    RecentSearchItemView itemView = new RecentSearchItemView((SearchFragment) getParentFragment(), item);
                    itemPanel.addView(itemView, offset + length);
                }
            } else if (op == Update.Op.REMOVE) {
                itemPanel.removeViewAt(offset);
            }
        });
        viewModel.loadEntrance();
    }

    public void onDestroyView() {
        super.onDestroyView();
    }

}
