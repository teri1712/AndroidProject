package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.DecadeApplication;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.home.fragment.SimpleLifecycleOwner;
import com.example.socialmediaapp.models.post.ImagePostModel;
import com.example.socialmediaapp.models.post.base.PostModel;
import com.example.socialmediaapp.utils.ImageSpec;
import com.example.socialmediaapp.utils.ImageUtils;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.button.LikeButton;
import com.example.socialmediaapp.view.button.PostButton;
import com.example.socialmediaapp.view.container.ClickablePanel;
import com.example.socialmediaapp.view.textview.PostContentTextVIew;
import com.example.socialmediaapp.view.action.LikeHelper;
import com.example.socialmediaapp.models.post.MediaPostModel;
import com.example.socialmediaapp.models.user.UserBasicInfoModel;
import com.example.socialmediaapp.viewmodel.item.PostViewModel;

public class PostItemView extends FrameLayout {
  private SimpleLifecycleOwner lifecycleOwner;
  private CircleButton avatarButton, erasePostButton;
  private TextView fullnameTextView;
  private PostContentTextVIew postContentTextVIew;
  private ClickablePanel mediaContainer;
  private ViewGroup mainContentPanel;
  private TextView countLike, countComment, countTime, countShare;
  private PostButton comment, share;
  private LikeButton likeButton;
  private PostViewModel viewModel;

  private void initOnClick(HandlerAccess handlerAccess, PostModel postModel) {
    HomePage activity = (HomePage) getContext();
    PostSessionHandler handler = handlerAccess.access();
    likeButton.initLikeView(lifecycleOwner, (MutableLiveData<Boolean>) viewModel.getLike());
    likeButton.setLikeHelper(new LikeHelper() {
      @Override
      public void like() {
        handler.doLike();
      }

      @Override
      public void unlike() {
        handler.doUnLike();
      }
    });

    comment.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View view) {
        activity.openCommentFragment(handlerAccess);
      }
    });
    avatarButton.setOnClickListener(view -> activity.openViewProfileFragment(postModel.getAuthor()));
  }

  private void initUserInfoContent(UserBasicInfoModel userModel) {
    fullnameTextView.setText(userModel.getFullname());
    avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), userModel.getScaled()), 0);
  }

  private ImageView initImageView(ImagePostModel postItem, HandlerAccess handlerAccess) {
    ImageSpec spec = postItem.getImageSpec();

    ImageView imageView = new ImageView(getContext());
    imageView.setScaleType(ImageView.ScaleType.CENTER);

    ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(spec.w, spec.h);
    imageView.setLayoutParams(params);

    LiveData<Bitmap> bitmap = ImageUtils.load(postItem.getImageUri());
    bitmap.observe(lifecycleOwner, new Observer<Bitmap>() {
      @Override
      public void onChanged(Bitmap bitmap) {
        BitmapDrawable drawable = new BitmapDrawable(getResources(), bitmap);
        imageView.setImageDrawable(drawable);
      }
    });
    mediaContainer.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        int p[] = new int[2];
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
          statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }
        imageView.getLocationInWindow(p);
        p[1] -= statusBarHeight;
        Rect bound = new Rect(0, 0, imageView.getWidth(), imageView.getHeight());
        bound.top += p[1];
        bound.bottom += p[1];
        bound.left += p[0];
        bound.right += p[0];
        HomePage activity = (HomePage) getContext();
        activity.openViewImagePostFragment(handlerAccess, bound);
      }
    });
    return imageView;
  }

  private PlayerView initMediaPlayer(MediaPostModel postItem) {
    PlayerView vid = new PlayerView(getContext());
    mediaContainer.setOnClickListener(null);
    ExoPlayer player = new ExoPlayer.Builder(getContext()).build();
    player.setMediaItem(MediaItem.fromUri(DecadeApplication.localhost + "/media/video/" + postItem.getMediaId()));
    player.prepare();
    vid.setPlayer(player);
    return vid;
  }

  private void initMediaContent(PostModel postModel, HandlerAccess handlerAccess) {
    String type = postModel.getType();
    if (type.equals("text")) {
      mediaContainer.setVisibility(GONE);
      return;
    }
    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT);
    params.gravity = Gravity.CENTER;
    View mediaView = null;
    if (type.startsWith("image")) {
      mediaView = initImageView((ImagePostModel) postModel, handlerAccess);
    } else if (type.startsWith("video")) {
      mediaView = initMediaPlayer((MediaPostModel) postModel);
    }
    type = type.split("/")[0];
    mediaView.setTag(type);
    mediaView.setLayoutParams(params);
    mediaContainer.addView(mediaView);
  }

  private void initStatusContent(PostModel postModel) {
    String status = postModel.getContent();
    if (status != null) {
      postContentTextVIew.setStatusContent(status);
    } else {
      mainContentPanel.removeView(postContentTextVIew);
      postContentTextVIew = null;
    }
  }

  public PostViewModel getViewModel() {
    return viewModel;
  }

  public PostItemView(@NonNull Context context) {
    super(context);

    lifecycleOwner = new SimpleLifecycleOwner();
    LayoutInflater inflater = LayoutInflater.from(context);
    ViewGroup root = (ViewGroup) inflater.inflate(R.layout.item_post, this, false);
    addView(root);

    countLike = root.findViewById(R.id.cnt_like);
    countComment = root.findViewById(R.id.cnt_comment);
    countShare = root.findViewById(R.id.cnt_share);
    mediaContainer = root.findViewById(R.id.media_container);
    avatarButton = root.findViewById(R.id.avatar);
    postContentTextVIew = root.findViewById(R.id.status_content);
    fullnameTextView = root.findViewById(R.id.fullname);
    mainContentPanel = root.findViewById(R.id.main_content_panel);
    countTime = root.findViewById(R.id.cnt_time);
    likeButton = root.findViewById(R.id.like_button);
    comment = root.findViewById(R.id.comment_button);
    share = root.findViewById(R.id.share_button);
    erasePostButton = root.findViewById(R.id.erase_post_button);

    mediaContainer.setFocusable(false);
    mediaContainer.setFocusableInTouchMode(false);
    avatarButton.setFocusable(false);
    avatarButton.setFocusableInTouchMode(false);
    comment.setFocusable(false);
    comment.setFocusableInTouchMode(false);

    likeButton.setFocusable(false);
    likeButton.setFocusableInTouchMode(false);
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
      mediaContainer.removeAllViews();
      lifecycleOwner.destroy();
      viewModel.clean();
    }
  }

  public void initViewModel(@NonNull HandlerAccess handlerAccess) {
    ensureNew();
    lifecycleOwner = new SimpleLifecycleOwner();
    lifecycleOwner.resume();
    PostSessionHandler handler = handlerAccess.access();
    LiveData<PostModel> postModelLiveData = handler.getPostData();
    LiveData<String> userHost = ((HomePage) getContext())
            .getViewModel()
            .getFullname();

    viewModel = new PostViewModel(postModelLiveData, userHost);
    PostModel postModel = postModelLiveData.getValue();
    initStatusContent(postModel);
    initUserInfoContent(postModel.getAuthor());
    initMediaContent(postModel, handlerAccess);

    viewModel.getCountLikeContent().observe(lifecycleOwner, s -> {
      if (s.isEmpty()) {
        countLike.setVisibility(GONE);
      } else {
        countLike.setText(s);
        countLike.setVisibility(VISIBLE);
      }
    });
    viewModel.getCountComment().observe(lifecycleOwner, integer -> {
      if (integer == 0) {
        countComment.setVisibility(GONE);
      } else {
        String txt = integer + " comments";
        countComment.setText(txt);
        if (countComment.getVisibility() == GONE) {
          countComment.setVisibility(VISIBLE);
        }
      }
    });
    viewModel.getCountShare().observe(lifecycleOwner, integer -> {
      if (integer == 0) {
        countShare.setVisibility(GONE);
      } else {
        String txt = integer + " shares";
        countShare.setText(txt);
        if (countShare.getVisibility() == GONE) {
          countShare.setVisibility(VISIBLE);
        }
      }
    });
    viewModel.getTime().observe(lifecycleOwner, s -> countTime.setText(s));
    initOnClick(handlerAccess, postModel);
  }
}
