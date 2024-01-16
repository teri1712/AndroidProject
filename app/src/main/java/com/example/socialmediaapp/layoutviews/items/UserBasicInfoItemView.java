package com.example.socialmediaapp.layoutviews.items;

import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.view.AvatarView;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.container.ClickablePanel;
import com.example.socialmediaapp.home.fragment.SearchFragment;
import com.example.socialmediaapp.home.fragment.RecentSearchFragment;
import com.example.socialmediaapp.viewmodel.fragment.RecentSearchFragmentViewModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class UserBasicInfoItemView extends ClickablePanel {
   protected UserBasicInfoModel userBasicInfoModel;
   private View root;
   private AvatarView avatarView;
   protected CircleButton eraseButton;
   private TextView fullname, alias;
   protected HomePage homePage;
   protected LifecycleOwner lifecycleOwner;
   protected SearchFragment owner;

   public UserBasicInfoItemView(SearchFragment owner, UserBasicInfoModel userBasicInfoModel) {
      super(owner.getContext());
      this.owner = owner;
      lifecycleOwner = owner.getViewLifecycleOwner();
      this.userBasicInfoModel = userBasicInfoModel;
      homePage = (HomePage) owner.getActivity();
      LayoutInflater inflater = LayoutInflater.from(getContext());
      root = inflater.inflate(R.layout.item_user_basic_info, this, false);
      addView(root);
      fullname = root.findViewById(R.id.fullname);
      eraseButton = root.findViewById(R.id.erase_button);
      avatarView = root.findViewById(R.id.avatar);
      alias = root.findViewById(R.id.alias);

      avatarView.setBackgroundContent(new BitmapDrawable(getResources(), userBasicInfoModel.getAvatar()), 0);
      fullname.setText(userBasicInfoModel.getFullname());
      alias.setText(userBasicInfoModel.getAlias());
      setOnClickListener(view -> {
         homePage.openViewProfileFragment(userBasicInfoModel);
         actionOfOnClick();
      });
   }

   protected void actionOfOnClick() {
      RecentSearchFragment recentSearchFragment = (RecentSearchFragment) owner
              .getChildFragmentManager()
              .findFragmentByTag("recent search");
      RecentSearchFragmentViewModel viewModel = recentSearchFragment.getViewModel();
      LiveData<String> dummy = viewModel.onClickToUserProfile(userBasicInfoModel.getId());
      dummy.observe(lifecycleOwner, s -> {
         // dummy
      });
   }

}
