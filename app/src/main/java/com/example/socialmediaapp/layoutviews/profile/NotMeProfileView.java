package com.example.socialmediaapp.layoutviews.profile;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.view.button.RoundedButton;
import com.example.socialmediaapp.layoutviews.profile.base.ProfileView;
import com.example.socialmediaapp.layoutviews.profile.model.Configuror;

public class NotMeProfileView extends ProfileView {
  private RoundedButton blueButton, greyButton;

  public RoundedButton getBlueButton() {
    return blueButton;
  }

  public RoundedButton getGreyButton() {
    return greyButton;
  }

  private Configuror config;

  @Override
  protected void initOnClick() {
    super.initOnClick();
    blueButton = root.findViewById(R.id.blue_button);
    greyButton = root.findViewById(R.id.grey_button);
    blueButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        config.leftAction();
      }
    });
    greyButton.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        config.rightAction();
      }
    });
  }

  public NotMeProfileView(@NonNull Fragment owner) {
    super(owner, R.layout.item_stranger_profile);
  }

  public void setConfiguration(Configuror configuror) {
    config = configuror;
    config.configure();
  }
}
