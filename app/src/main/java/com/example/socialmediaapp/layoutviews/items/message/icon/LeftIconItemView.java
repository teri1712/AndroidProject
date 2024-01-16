package com.example.socialmediaapp.layoutviews.items.message.icon;

import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.home.fragment.message.MessageFragment;
import com.example.socialmediaapp.layoutviews.items.message.MessageItemView;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.view.AvatarView;

public class LeftIconItemView extends MessageItemView {
  private View msgView;
  private AvatarView avatarView;

  public LeftIconItemView(MessageFragment owner) {
    super(owner);
  }

  @Override
  public void initViewModel(ChatSessionModel chatModel, MessageItemModel model) {
    super.initViewModel(chatModel, model);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.message_left, root, true);

    ViewGroup msgContainer = root.findViewById(R.id.message_content);

    int t = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    int b = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics());
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msgContainer.getLayoutParams();
    params.topMargin = t;
    params.bottomMargin = b;

    msgView = inflater.inflate(R.layout.message_icon, msgContainer, false);

    msgContainer.addView(msgView);
    initAvatarView(chatModel.getOther());
  }

  private void initAvatarView(UserBasicInfoModel other) {
    avatarView = root.findViewById(R.id.avatar_view);
    avatarView.setBackgroundContent(new BitmapDrawable(getResources(), other.getScaled()), -1);

  }
}
