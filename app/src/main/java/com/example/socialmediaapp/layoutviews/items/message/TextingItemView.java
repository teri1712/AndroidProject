package com.example.socialmediaapp.layoutviews.items.message;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.view.AvatarView;

public class TextingItemView extends FrameLayout {
  private TextingAnimateView animateView;
  private AvatarView avatarView;

  public TextingItemView(@NonNull Context context) {
    super(context);
    LayoutInflater inflater = LayoutInflater.from(context);
    inflater.inflate(R.layout.message_texting, this, true);
    animateView = findViewById(R.id.texting_view);
    avatarView = findViewById(R.id.avatar_view);
  }

  public void init(UserBasicInfoModel other) {
    avatarView.setBackgroundContent(new BitmapDrawable(getResources(), other.getScaled()), -1);
  }

  @Override
  public void setVisibility(int visibility) {
    if (visibility == VISIBLE) {
      animateView.performAnimation();
    } else {
      animateView.endAnimation();
    }
    super.setVisibility(visibility);
  }
}
