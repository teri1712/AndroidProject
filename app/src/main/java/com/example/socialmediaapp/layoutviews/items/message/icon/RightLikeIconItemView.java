package com.example.socialmediaapp.layoutviews.items.message.icon;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.network.PendMonitor;
import com.example.socialmediaapp.application.MessageMonitorStore;
import com.example.socialmediaapp.home.fragment.message.MessageFragment;
import com.example.socialmediaapp.layoutviews.items.message.MessageItemView;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.view.AvatarView;

public class RightLikeIconItemView extends MessageItemView {
  private AvatarView seenView;
  private View msgView;
  private TextView pendView;

  public RightLikeIconItemView(MessageFragment owner) {
    super(owner);
  }

  @Override
  public void initViewModel(ChatSessionModel chatModel, MessageItemModel model) {
    super.initViewModel(chatModel, model);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.message_right, root, true);
    ViewGroup msgContainer = root.findViewById(R.id.message_content);

    int t = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
    int b = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) msgContainer.getLayoutParams();
    params.topMargin = t;
    params.bottomMargin = b;

    msgView = inflater.inflate(R.layout.message_icon, msgContainer, false);

    msgContainer.addView(msgView);
    initMsgState(chatModel, model);
  }

  private void initMsgState(ChatSessionModel chatModel, MessageItemModel model) {
    pendView = root.findViewById(R.id.pending_view);
    seenView = root.findViewById(R.id.seen_view);
    UserBasicInfoModel other = chatModel.getOther();
    LiveData<Long> lastSeen = chatModel.getOnlineChat().getLastSeen();
    final Long msgTime = model.getTime();

    if (model.isUnCommitted()) {
      LiveData<String> msgState = MessageMonitorStore.getInstance().findMessageStateLiveData(model.getMsgId());
      if (msgState != null) {
        msgState.observe(lifecycleOwner, s -> {
          switch (s) {
            case "In progress":
              pendView.setVisibility(VISIBLE);
              pendView.setText("Sending");
              break;
            case "Complete":
              pendView.setVisibility(GONE);
              msgState.removeObservers(lifecycleOwner);
              break;
            default:
              assert false;
          }
        });
      }
      /* committed */
    }

    seenView.setBackgroundContent(new BitmapDrawable(getResources(), other.getScaled()), -1);
    lastSeen.observe(lifecycleOwner, new Observer<Long>() {
      @Override
      public void onChanged(Long aLong) {
        if (aLong.equals(msgTime)) {
          seenView.setVisibility(VISIBLE);
        } else {
          seenView.setVisibility(GONE);
          if (aLong > msgTime) {
            lastSeen.removeObserver(this);
          }
        }
      }
    });
  }
}
