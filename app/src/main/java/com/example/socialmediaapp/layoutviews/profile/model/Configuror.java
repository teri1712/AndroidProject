package com.example.socialmediaapp.layoutviews.profile.model;


import androidx.annotation.NonNull;

import com.example.socialmediaapp.application.session.ProfileSessionHandler;
import com.example.socialmediaapp.layoutviews.profile.NotMeProfileView;
import com.example.socialmediaapp.view.button.RoundedButton;

public abstract class Configuror {
  protected ProfileSessionHandler handler;
  protected NotMeProfileView profileView;
  protected RoundedButton leftButton, rightButton;

  public Configuror(@NonNull NotMeProfileView profileView) {
    this.profileView = profileView;
    this.handler = profileView.getViewModel().getHandler();
    this.leftButton = profileView.getBlueButton();
    this.rightButton = profileView.getGreyButton();
  }

  public abstract void configure();

  public abstract void leftAction();

  public abstract void rightAction();

}
