package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.ReplyAccessHandler;
import com.example.socialmediaapp.home.fragment.SimpleLifecycleOwner;
import com.example.socialmediaapp.models.post.CommentModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.textview.ClickableTextView;
import com.example.socialmediaapp.view.textview.LikeTextView;
import com.example.socialmediaapp.viewmodel.item.CommentViewModel;
import com.example.socialmediaapp.view.action.LikeHelper;

public class CommentItemView extends FrameLayout {
  private SimpleLifecycleOwner lifecycleOwner;
  private CommentViewModel viewModel;
  private ViewGroup root;
  private CircleButton avatarButton;
  private TextView fullname, content;
  private ImageView imageContent;
  private TextView cntTime, cntLike;
  private ClickableTextView replyTextView;
  private LikeTextView likeTextView;
  private FrameLayout backgroundPanel;
  private ViewGroup infoPanel, mainContentPanel;
  private boolean willDrawBranch;

  public CommentItemView(@NonNull Context context) {
    super(context);

    setFocusable(false);
    setFocusableInTouchMode(false);
    setWillNotDraw(false);
    LayoutInflater inflater = LayoutInflater.from(context);
    root = (ViewGroup) inflater.inflate(R.layout.item_comment, this, false);

    addView(root);
    fullname = root.findViewById(R.id.fullname);
    avatarButton = root.findViewById(R.id.avatar_button);
    content = root.findViewById(R.id.comment_content);
    imageContent = root.findViewById(R.id.image_view);
    likeTextView = root.findViewById(R.id.like_text_view);
    replyTextView = root.findViewById(R.id.reply_text_view);
    cntTime = root.findViewById(R.id.cnt_time);
    cntLike = root.findViewById(R.id.cnt_like);
    imageContent = root.findViewById(R.id.image_view);
    mainContentPanel = root.findViewById(R.id.main_content_panel);
    backgroundPanel = root.findViewById(R.id.background_panel);
    infoPanel = root.findViewById(R.id.information_panel);
  }

  private void initContent(CommentModel commentModel) {
    UserBasicInfoModel userModel = commentModel.getAuthor();
    fullname.setText(userModel.getFullname());
    avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), userModel.getScaled()), 0);

    if (commentModel.getContent() != null && !commentModel.getContent().isEmpty()) {
      content.setText(commentModel.getContent());
      content.setVisibility(VISIBLE);
      backgroundPanel.setWillNotDraw(false);
    }
    ImageSpec imageSpec = commentModel.getImageSpec();
    if (imageSpec != null) {
      ViewGroup.LayoutParams params = imageContent.getLayoutParams();
      params.width = imageSpec.w;
      params.height = imageSpec.h;
      imageContent.requestLayout();
      String imageUri = commentModel.getImageUri();

      LiveData<Bitmap> image = ImageUtils.loadWithSpec(imageSpec, imageUri);
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
        homePage.openViewImageFragment(imageUri);
      });
    }
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

  public void initViewModel(CommentSessionHandler commentHandler) {
    ensureNew();

    lifecycleOwner = new SimpleLifecycleOwner();
    lifecycleOwner.resume();
    willDrawBranch = false;

    ReplyAccessHandler replyAccessHandler = commentHandler.getReplyDataAccess();
    LiveData<Integer> totalComment = replyAccessHandler.getTotalComment();
    totalComment.observe(lifecycleOwner, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer > 0) {
          willDrawBranch = true;
          totalComment.removeObserver(this);
          invalidate();
        }
      }
    });

    LiveData<CommentModel> commentModelLiveData = commentHandler.getCommentData();
    viewModel = new CommentViewModel(commentModelLiveData);
    CommentModel commentModel = commentModelLiveData.getValue();
    initContent(commentModel);

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
    initOnClick(commentHandler);
  }

  public void setReplyButtonAction(Runnable action) {
    replyTextView.setOnClickListener(view -> action.run());
  }

  private void initOnClick(CommentSessionHandler handler) {
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
    UserBasicInfoModel userModel = handler.getCommentData().getValue().getAuthor();
    avatarButton.setOnClickListener(view -> {
      HomePage activity = (HomePage) getContext();
      activity.openViewProfileFragment(userModel);
    });
  }

  @Override
  protected void onDraw(Canvas canvas) {
    if (willDrawBranch) {
      Paint treeStroke = new Paint();
      treeStroke.setColor(Color.parseColor("#dee2e6"));
      treeStroke.setAntiAlias(true);
      treeStroke.setStyle(Paint.Style.STROKE);
      treeStroke.setStrokeWidth(5);
      Rect rect = new Rect(0, 0, avatarButton.getWidth(), avatarButton.getHeight());
      mainContentPanel.offsetDescendantRectToMyCoords(avatarButton, rect);
      int x = (rect.left + rect.right) / 2, y = rect.bottom + 15;
      Path path = new Path();
      path.moveTo(x, y);
      path.lineTo(x, getHeight());

      canvas.drawPath(path, treeStroke);
    }
    super.onDraw(canvas);
  }
}
