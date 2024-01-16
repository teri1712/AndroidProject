package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.button.RoundedButton;

public class FriendRequestItem extends FrameLayout {
   private View root;
   private TextView fullname, time;
   private CircleButton avatarButton;
   private RoundedButton accept, refuse;

   public FriendRequestItem(@NonNull Context context) {
      super(context);

      LayoutInflater inflater = LayoutInflater.from(context);
      root = (ViewGroup) inflater.inflate(R.layout.friend_request_item, this, false);
      addView(root);
   }


}
