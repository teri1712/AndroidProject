package com.example.socialmediaapp.layoutviews.items.message;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.application.MessageMonitorStore;
import com.example.socialmediaapp.layoutviews.items.message.layout.PositionedItem;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.view.AvatarView;
import com.example.socialmediaapp.view.message.MessageClipFrame;
import com.example.socialmediaapp.view.message.RoundedClipMessage;
import com.example.socialmediaapp.home.fragment.message.MessageFragment;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.message.ImageMessageItemModel;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.models.messenger.message.TextMessageItemModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class RightMessageItemView extends MessageItemView implements PositionedItem {
  private AvatarView seenView;
  private View msgView;
  private TextView pendView;
  private MessageClipFrame clipFrame;

  public RightMessageItemView(MessageFragment owner) {
    super(owner);
  }

  @Override
  public void initViewModel(ChatSessionModel chatModel, MessageItemModel msg) {
    super.initViewModel(chatModel, msg);
    LayoutInflater inflater = LayoutInflater.from(getContext());
    inflater.inflate(R.layout.message_right, root, true);
    ViewGroup msgContainer = root.findViewById(R.id.message_content);

    switch (msg.getType()) {
      case "text": {
        TextMessageItemModel textMsg = (TextMessageItemModel) msg;
        msgView = inflater.inflate(R.layout.message_text, msgContainer, false);
        msgView.setBackground(null);
        TextView textView = msgView.findViewById(R.id.text_content);
        textView.setText(textMsg.getText());
        msgView.findViewById(R.id.main_text_frame).setBackground(new ColorDrawable(Color.parseColor("#0084ff")));
        break;
      }
      case "image": {
        ImageMessageItemModel imageMsg = (ImageMessageItemModel) msg;
        msgView = inflater.inflate(R.layout.message_image, msgContainer, false);
        ImageView imageView = msgView.findViewById(R.id.image_content);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        ImageSpec imageSpec = imageMsg.getImageSpec();
        String imageUri = imageMsg.getImageUri();

        params.width = imageSpec.w;
        params.height = imageSpec.h;
        LiveData<Bitmap> image = ImageUtils.loadWithSpec(imageSpec, imageUri);
        image.observe(lifecycleOwner, new Observer<Bitmap>() {
          @Override
          public void onChanged(Bitmap bitmap) {
            imageView.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
          }
        });

        imageView.setClickable(true);
        imageView.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
            MessageHome messageHome = (MessageHome) getContext();
            messageHome.openViewImageFragment(imageMsg.getImageUri());
          }
        });
        break;
      }
    }

    msgContainer.addView(msgView);
    clipFrame = msgView.findViewById(R.id.msg_clipped_view);
    initMsgState(chatModel, msg);
  }

  @Override
  public void onPositionChanged(int pos) {
    clipFrame.setClipHelper(new RoundedClipMessage(pos, RoundedClipMessage.LEFT));
  }

  private void initMsgState(ChatSessionModel chatSessionModel, MessageItemModel model) {
    pendView = root.findViewById(R.id.pending_view);
    seenView = root.findViewById(R.id.seen_view);
    UserBasicInfoModel other = chatSessionModel.getOther();
    LiveData<Long> lastSeen = chatSessionModel.getOnlineChat().getLastSeen();
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
