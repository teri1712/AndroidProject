package com.example.socialmediaapp.layoutviews.items;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.LikeButton;
import com.example.socialmediaapp.customview.button.PostButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.textview.PostContentTextVIew;
import com.example.socialmediaapp.viewmodels.items.PostItemViewModel;
import com.example.socialmediaapp.viewmodels.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.post.MediaPost;
import com.example.socialmediaapp.viewmodels.models.post.base.Post;
import com.example.socialmediaapp.viewmodels.models.user.UserBasicInfo;

public class PostItemView extends FrameLayout {
    private ViewGroup root;
    private CircleButton avatarButton, erasePostButton;
    private PostContentTextVIew postContentTextVIew;
    private TextView fullnameTextView;
    private ClickablePanel mediaContainer;
    private ViewGroup mainContentPanel;
    private TextView countLike, countComment, countTime, countShare;
    private PostButton comment, share;
    private LikeButton like;
    private PostItemViewModel viewModel;
    private ItemRepository<Post> repo;
    private LifecycleOwner lifecycleOwner;

    public LifecycleOwner getViewLifecycleOwner() {
        return lifecycleOwner;
    }

    private ImageView initImageView(ImagePost postItem) {
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(postItem.getImage());
        imageView.setScaleType(ImageView.ScaleType.CENTER);
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
                activity.openViewImageFragment(bound, postItem);
            }
        });
        return imageView;
    }

    private PlayerView initMediaPlayer(MediaPost postItem) {
        PlayerView vid = new PlayerView(getContext());
        mediaContainer.setOnClickListener(null);
        ExoPlayer player = new ExoPlayer.Builder(getContext()).build();
        player.setMediaItem(MediaItem.fromUri(ApplicationContainer.getInstance().localhost + "/media/video/" + postItem.getMediaId()));
        player.prepare();
        vid.setPlayer(player);
        return vid;
    }

    private void initOnClick(Post post) {
        HomePage activity = (HomePage) getContext();
        comment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openCommentFragment(viewModel);
            }
        });
        avatarButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openViewProfileFragment(post.getAuthor());
            }
        });
        erasePostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                repo.remove(viewModel.getPost());
            }
        });
    }

    private void initUserInfoContent(Post post) {
        avatarButton.setBackgroundContent(post.getAuthor().getAvatar(), 0);
        UserBasicInfo author = post.getAuthor();
        fullnameTextView.setText(author.getFullname());
    }

    private void initMediaContent(Post post) {
        if (post.getType() == null) {
            mainContentPanel.removeView(mediaContainer);
            mediaContainer = null;
            return;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        View mediaView = null;
        if (post.getType().equals("image")) {
            mediaView = initImageView((ImagePost) post);
        } else {
            mediaView = initMediaPlayer((MediaPost) post);
        }
        mediaView.setLayoutParams(params);
        mediaContainer.addView(mediaView);
        mediaContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int max_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 500, getContext().getResources().getDisplayMetrics());
                ViewGroup.LayoutParams p = mediaContainer.getLayoutParams();
                p.height = Math.min(max_height, mediaContainer.getHeight());
                mediaContainer.requestLayout();
                mediaContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initStatusContent(String status) {
        if (status != null) {
            postContentTextVIew.setStatusContent(status);
        } else {
            mainContentPanel.removeView(postContentTextVIew);
            postContentTextVIew = null;
        }
    }

    private void initViewData(Post post) {
        initStatusContent(post.getStatus());
        initUserInfoContent(post);
        initMediaContent(post);
        initOnClick(post);
    }

    public PostItemView(Fragment owner, ItemRepository<Post> repo, int pos) {
        super(owner.getContext());
        lifecycleOwner = owner.getViewLifecycleOwner();
        this.repo = repo;
        LayoutInflater inflater = LayoutInflater.from(owner.getContext());
        root = (ViewGroup) inflater.inflate(R.layout.post_item, this, false);
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
        like = root.findViewById(R.id.like_button);
        comment = root.findViewById(R.id.comment_button);
        share = root.findViewById(R.id.share_button);
        erasePostButton = root.findViewById(R.id.erase_post_button);

        mediaContainer.setFocusable(false);
        mediaContainer.setFocusableInTouchMode(false);
        avatarButton.setFocusable(false);
        avatarButton.setFocusableInTouchMode(false);
        comment.setFocusable(false);
        comment.setFocusableInTouchMode(false);

        like.setFocusable(false);
        like.setFocusableInTouchMode(false);

        viewModel = new PostItemViewModel(repo.getItem(pos));
        initViewModel();

    }

    public void initViewModel() {
        initViewData(viewModel.getPost());
        viewModel.getCountLikeContent().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null || s.isEmpty()) {
                    countLike.setVisibility(GONE);
                } else {
                    countLike.setText(s);
                    if (countLike.getVisibility() == GONE) {
                        countLike.setVisibility(VISIBLE);
                    }
                }
            }
        });
        viewModel.getCountComment().observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null || integer == 0) {
                    countComment.setVisibility(GONE);
                } else {
                    String txt = Integer.toString(integer) + " comments";
                    countComment.setText(txt);
                    if (countComment.getVisibility() == GONE) {
                        countComment.setVisibility(VISIBLE);
                    }
                }
            }
        });
        viewModel.getCountShare().observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == null || integer == 0) {
                    countShare.setVisibility(GONE);
                } else {
                    String txt = Integer.toString(integer) + " shares";
                    countShare.setText(txt);
                    if (countShare.getVisibility() == GONE) {
                        countShare.setVisibility(VISIBLE);
                    }
                }
            }
        });
        viewModel.getTime().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                countTime.setText(s);
            }
        });

        like.initLikeAction(lifecycleOwner, viewModel.getIsLiked(), viewModel.getPost());
    }
}
