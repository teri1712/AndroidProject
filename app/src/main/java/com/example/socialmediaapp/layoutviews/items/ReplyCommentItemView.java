package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.application.session.ReplySessionHandler;
import com.example.socialmediaapp.home.fragment.SimpleLifecycleOwner;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.textview.LikeTextView;
import com.example.socialmediaapp.viewmodel.item.ReplyViewModel;
import com.example.socialmediaapp.view.action.LikeHelper;
import com.example.socialmediaapp.models.post.ReplyModel;

public class ReplyCommentItemView extends FrameLayout {
  private SimpleLifecycleOwner lifecycleOwner;
  private ReplyViewModel viewModel;
  private ViewGroup root;
  private CircleButton avatarButton;
  private TextView fullname, contentTextView;
  private ImageView imageContent;
  private TextView cntTime, cntLike;
  private LikeTextView likeTextView;
  private ViewGroup mainContentPanel;
  private FrameLayout backgroundPanel;
  private boolean isLastReply;

  public ReplyCommentItemView(Context context) {
    super(context);
    setFocusable(false);
    setFocusableInTouchMode(false);
    setWillNotDraw(false);
    LayoutInflater inflater = LayoutInflater.from(context);
    root = (ViewGroup) inflater.inflate(R.layout.item_reply_comment, this, false);

    addView(root);
    fullname = root.findViewById(R.id.fullname);
    avatarButton = root.findViewById(R.id.avatar_button);
    contentTextView = root.findViewById(R.id.comment_content);
    imageContent = root.findViewById(R.id.image_view);
    likeTextView = root.findViewById(R.id.like_text_view);
    cntTime = root.findViewById(R.id.cnt_time);
    cntLike = root.findViewById(R.id.cnt_like);
    imageContent = root.findViewById(R.id.image_view);
    mainContentPanel = root.findViewById(R.id.main_content_panel);
    backgroundPanel = root.findViewById(R.id.background_panel);
  }

  @Override
  protected void onDetachedFromWindow() {
    if (lifecycleOwner != null) {
      lifecycleOwner.destroy();
      viewModel.clean();
    }
    super.onDetachedFromWindow();
  }

  private void ensureNew() {
    if (lifecycleOwner != null) {
      lifecycleOwner.destroy();
      viewModel.clean();
    }
  }

  public void initViewModel(ReplySessionHandler handler) {
    ensureNew();

    lifecycleOwner = new SimpleLifecycleOwner();
    isLastReply = false;
    LiveData<ReplyModel> replyLiveData = handler.getReplyData();
    ReplyModel replyModel = replyLiveData.getValue();
    initContent(replyModel);
    viewModel = new ReplyViewModel(replyLiveData);
    viewModel.getTime().observe(lifecycleOwner, s -> cntTime.setText(s));
    viewModel.getCountLikeContent().observe(lifecycleOwner, s -> {
      if (s.isEmpty()) {
        cntLike.setVisibility(GONE);
      } else {
        cntLike.setText(s);
        cntLike.setVisibility(VISIBLE);
      }
    });
    likeTextView.initLikeView(lifecycleOwner, viewModel.getLike());
    initOnClick(handler);
  }

  private void initContent(ReplyModel replyModel) {
    UserBasicInfoModel userModel = replyModel.getSender();
    fullname.setText(userModel.getFullname());
    avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), userModel.getScaled()), 0);
    String content = replyModel.getContent();

    if (content != null && !content.isEmpty()) {
      contentTextView.setText(content);
      contentTextView.setVisibility(VISIBLE);
      backgroundPanel.setWillNotDraw(false);
    }
    ImageSpec imageSpec = replyModel.getImageSpec();
    String imageUri = replyModel.getImageUri();
    if (imageSpec != null) {
      ViewGroup.LayoutParams params = imageContent.getLayoutParams();
      params.width = imageSpec.w;
      params.height = imageSpec.h;
      imageContent.requestLayout();

      LiveData<Bitmap> image = ImageUtils.load(imageUri);
      image.observe(lifecycleOwner, new Observer<Bitmap>() {
        @Override
        public void onChanged(Bitmap bitmap) {
          imageContent.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
        }
      });

      imageContent.setVisibility(VISIBLE);
      imageContent.setClickable(true);
      imageContent.setOnClickListener(view -> {
        HomePage homePage = (HomePage) getContext();
        homePage.openViewImageFragment(replyModel.getImageUri());
      });
    }
  }

  private void initOnClick(ReplySessionHandler handler) {
    likeTextView.setLikeHelper(new LikeHelper() {
      @Override
      public void like() {
        handler.doLike();
      }

      @Override
      public void unlike() {
        handler.doUnLike();
      }
    });
    UserBasicInfoModel userModel = handler.getReplyData().getValue().getSender();
    avatarButton.setOnClickListener(view -> {
      HomePage activity = (HomePage) getContext();
      activity.openViewProfileFragment(userModel);
    });
  }

  public void setLastReply(boolean lastReply) {
    isLastReply = lastReply;
    invalidate();
  }

  public void offsetOfAvatar(Rect rect) {
    rect.bottom = avatarButton.getHeight();
    mainContentPanel.offsetDescendantRectToMyCoords(avatarButton, rect);
    root.offsetDescendantRectToMyCoords(mainContentPanel, rect);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    Paint treeStroke = new Paint();
    treeStroke.setColor(Color.parseColor("#dee2e6"));
    treeStroke.setAntiAlias(true);
    treeStroke.setStyle(Paint.Style.STROKE);
    treeStroke.setStrokeWidth(4.5f);

    int x = (int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            30,
            getContext().getResources().getDisplayMetrics());
    int y = 0;

    Rect rect = new Rect(0, 0, avatarButton.getWidth(), avatarButton.getHeight());
    offsetOfAvatar(rect);
    int targetY = (rect.top + rect.bottom) / 2;
    int targetX = rect.left;

    Path path = new Path();
    path.moveTo(x, targetY - 20);
    path.arcTo(x, targetY - 40, x + 40, targetY, -180f, -90f, false);
//
//      path.quadTo(x, targetY, x + 25, targetY);
    path.lineTo(targetX - 10, targetY);
    path.moveTo(x, y);
    path.lineTo(x, isLastReply ? (targetY - 20) : getHeight());
    canvas.drawPath(path, treeStroke);

    super.onDraw(canvas);
  }
}
