package com.example.socialmediaapp.home.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.layoutviews.items.RecentSearchItemView;
import com.example.socialmediaapp.viewmodel.fragment.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

import java.util.HashMap;

public class RecentSearchFragment extends Fragment {

   public RecentSearchFragment() {
   }

   private ViewGroup itemPanel;
   private RecentSearchFragmentViewModel viewModel;
   private CustomSpinningView loadSpinner;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_recent_search, container, false);
      loadSpinner = view.findViewById(R.id.load_spinner);
      itemPanel = view.findViewById(R.id.posts_panel);
      RecentSearchAccessHandler recentHandler = ((HomePage) getActivity())
              .getViewModel()
              .getHandler()
              .getSearchHandler()
              .getRecentHandler();

      viewModel = new RecentSearchFragmentViewModel(recentHandler);


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
      Repository<UserBasicInfoModel> repository = viewModel.getItemRepository();
      LiveData<Update> itemUpdate = repository.getItemUpdate();
      itemUpdate.observe(getViewLifecycleOwner(), update -> {
         if (update == null) return;
         Update.Op op = update.op;
         HashMap<String, Object> data = update.data;
         int offset = (int) data.get("offset");
         if (op == Update.Op.ADD) {
            int length = (int) data.get("length");
            for (int i = 0; i < length; i++) {
               UserBasicInfoModel item = repository.get(offset + i);
               RecentSearchItemView itemView = new RecentSearchItemView((SearchFragment) getParentFragment(), item);
               itemPanel.addView(itemView, offset + i);
            }
         } else if (op == Update.Op.REMOVE) {
            itemPanel.removeViewAt(offset);
         }
      });
      viewModel.load(10);
   }

   public void onDestroyView() {
      viewModel.getItemRepository().close();
      super.onDestroyView();
   }

}
