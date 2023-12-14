package com.example.socialmediaapp.home.fragment.main;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

   public static SettingsFragment newInstance(String param1, String param2) {
      SettingsFragment fragment = new SettingsFragment();
      return fragment;
   }

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   private RoundedButton logout;

   private CircleButton avatarButton;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container,
                            Bundle savedInstanceState) {
      // Inflate the layout for this fragment
      ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
      logout = view.findViewById(R.id.logout_button);
      avatarButton = view.findViewById(R.id.avatar_button);
      HomePage homePage = (HomePage) getActivity();
      UserSessionViewModel userSessionViewModel = homePage.getViewModel();
      userSessionViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
         @Override
         public void onChanged(Bitmap bitmap) {
            avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), bitmap), -1);
         }
      });
      initOnClick();
      return view;
   }

   private void initOnClick() {
      logout.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
            OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;
            onlineSessionHandler.logout();
         }
      });
      avatarButton.setOnClickListener(view -> {
         HomePage homePage = (HomePage) getActivity();
         UserSessionViewModel userSessionViewModel = homePage.getViewModel();

         UserBasicInfo userBasicInfo = new UserBasicInfo();
         userBasicInfo.setAvatar(userSessionViewModel.getAvatar().getValue());
         userBasicInfo.setFullname(userSessionViewModel.getFullname().getValue());
         userBasicInfo.setAlias(userSessionViewModel.getAlias().getValue());
         homePage.openViewProfileFragment(userBasicInfo);
      });
   }
}