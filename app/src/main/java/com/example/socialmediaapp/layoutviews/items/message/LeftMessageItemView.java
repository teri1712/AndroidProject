package com.example.socialmediaapp.layoutviews.items.message;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.layoutviews.items.message.layout.PositionedItem;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.utils.LiveDataBitmapTarget;
import com.example.socialmediaapp.view.AvatarView;
import com.example.socialmediaapp.view.message.MessageClipFrame;
import com.example.socialmediaapp.view.message.RoundedClipMessage;
import com.example.socialmediaapp.home.fragment.message.MessageFragment;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.message.ImageMessageItemModel;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.models.messenger.message.TextMessageItemModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;

public class LeftMessageItemView extends MessageItemView implements PositionedItem {
  private View msgView;
  private AvatarView avatarView;
  private MessageClipFrame messageClipFrame;

  public LeftMessageItemView(MessageFragment owner) {
    super(owner);
  }

  @Override
  public void initViewModel(ChatSessionModel chatSessionModel, MessageItemModel messageItemModel) {
    super.initViewModel(chatSessionModel, messageItemModel);
    LayoutInflater inflater = LayoutInflater.from(getContext());

    inflater.inflate(R.layout.message_left, root, true);

    ViewGroup msgContainer = root.findViewById(R.id.message_content);

    if (messageItemModel instanceof TextMessageItemModel) {
      TextMessageItemModel textMessageItem = (TextMessageItemModel) messageItemModel;
      msgView = inflater.inflate(R.layout.message_text, msgContainer, false);
      TextView textView = msgView.findViewById(R.id.text_content);
      textView.setText(textMessageItem.getText());
    } else if (messageItemModel instanceof ImageMessageItemModel) {
      ImageMessageItemModel imageMessage = (ImageMessageItemModel) messageItemModel;
      ImageSpec imageSpec = imageMessage.getImageSpec();
      String uri = imageMessage.getImageUri();

      msgView = inflater.inflate(R.layout.message_image, msgContainer, false);
      ImageView imageView = msgView.findViewById(R.id.image_content);
      ViewGroup.LayoutParams params = imageView.getLayoutParams();
      params.width = imageSpec.w;
      params.height = imageSpec.h;
      imageView.requestLayout();
      ImageUtils.loadWithSpec(imageSpec, uri).observe(lifecycleOwner, new Observer<Bitmap>() {
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
          messageHome.openViewImageFragment(imageMessage.getImageUri());
        }
      });
    }
    msgContainer.addView(msgView);

    messageClipFrame = root.findViewById(R.id.msg_clipped_view);
    messageClipFrame.setClipHelper(new RoundedClipMessage(
            RoundedClipMessage.START | RoundedClipMessage.END,
            RoundedClipMessage.RIGHT));

    initAvatarView(chatSessionModel.getOther());
  }

  private void initAvatarView(UserBasicInfoModel other) {
    avatarView = root.findViewById(R.id.avatar_view);
    avatarView.setBackgroundContent(new BitmapDrawable(getResources(), other.getScaled()), -1);
  }

  @Override
  public void onPositionChanged(int pos) {
    if ((pos & RoundedClipMessage.END) == RoundedClipMessage.END) {
      avatarView.setWillNotDraw(false);
    } else {
      avatarView.setWillNotDraw(true);
    }
    messageClipFrame.setClipHelper(new RoundedClipMessage(pos, RoundedClipMessage.RIGHT));
  }
}
