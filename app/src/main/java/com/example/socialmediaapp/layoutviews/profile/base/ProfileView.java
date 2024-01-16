package com.example.socialmediaapp.layoutviews.profile.base;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.models.post.ImagePostModel;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.home.fragment.information.ProfileFragment;
import com.example.socialmediaapp.viewmodel.ProfileViewModel;
import com.example.socialmediaapp.models.user.profile.base.ProfileModel;

public class ProfileView extends FrameLayout {

  protected View root;
  protected CircleButton avatar_button;
  protected ImageView background;
  protected TextView birthday, gender, fullname;
  protected Fragment owner;
  protected ProfileViewModel viewModel;
  protected LifecycleOwner lifecycleOwner;

  public LifecycleOwner getLifecycleOwner() {
    return lifecycleOwner;
  }

  public void initViewModel() {
    LiveData<ProfileModel> userProfile = viewModel.getLiveData();
    userProfile.observe(lifecycleOwner, new Observer<ProfileModel>() {
      @Override
      public void onChanged(ProfileModel profileModel) {
        birthday.setText(profileModel.getBirthday());
        gender.setText(profileModel.getGender());
        fullname.setText(profileModel.getFullname());
      }
    });
    LiveData<HandlerAccess> avatarAccess = viewModel.getAvatarAccess();
    avatarAccess.observe(lifecycleOwner, new Observer<HandlerAccess>() {
      @Override
      public void onChanged(HandlerAccess handlerAccess) {
        PostSessionHandler avtPostHandler = handlerAccess.access();
        avatar_button.setOnClickListener(view -> {
          HomePage homePage = (HomePage) getContext();
          homePage.openViewImagePostFragment(handlerAccess, null);
        });
        ImagePostModel model = (ImagePostModel) avtPostHandler.getPostData().getValue();
        String imageUri = model.getImageUri();
        LiveData<Bitmap> image = ImageUtils.load(imageUri);
        image.observe(lifecycleOwner, new Observer<Bitmap>() {
          @Override
          public void onChanged(Bitmap bitmap) {
            avatar_button.setBackgroundContent(new BitmapDrawable(getContext().getResources(), bitmap), 0);
          }
        });
      }
    });


    LiveData<HandlerAccess> bgAccess = viewModel.getBgAccess();
    bgAccess.observe(lifecycleOwner, new Observer<HandlerAccess>() {
      @Override
      public void onChanged(HandlerAccess handlerAccess) {
        PostSessionHandler bgPostHandler = handlerAccess.access();

        background.setOnClickListener(view -> {
          HomePage homePage = (HomePage) getContext();
          homePage.openViewImagePostFragment(handlerAccess, null);
        });
        ImagePostModel model = (ImagePostModel) bgPostHandler.getPostData().getValue();
        String imageUri = model.getImageUri();
        LiveData<Bitmap> image = ImageUtils.load(imageUri);
        image.observe(lifecycleOwner, new Observer<Bitmap>() {
          @Override
          public void onChanged(Bitmap bitmap) {
            background.setImageDrawable(new BitmapDrawable(getContext().getResources(), bitmap));
          }
        });
      }
    });
    initOnClick();
  }

  protected void initOnClick() {
  }

  public ProfileView(@NonNull Fragment owner, int resource) {
    super(owner.getContext());
    this.owner = owner;
    lifecycleOwner = owner.getViewLifecycleOwner();
    viewModel = ((ProfileFragment) owner).getViewModel();
    LayoutInflater inflater = LayoutInflater.from(getContext());
    root = inflater.inflate(resource, this, true);
    background = root.findViewById(R.id.background);
    avatar_button = root.findViewById(R.id.avatar_button);
    birthday = root.findViewById(R.id.birthday_textview);
    gender = root.findViewById(R.id.gender_textview);
    fullname = root.findViewById(R.id.fullname);
  }

  public ProfileViewModel getViewModel() {
    return viewModel;
  }
}
