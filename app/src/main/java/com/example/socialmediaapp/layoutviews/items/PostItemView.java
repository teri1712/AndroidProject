package com.example.socialmediaapp.layoutviews.items;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.LikeButton;
import com.example.socialmediaapp.customview.button.PostButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.textview.PostContentTextVIew;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.dunno.LikeHelper;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.post.MediaPost;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;
import com.example.socialmediaapp.viewmodel.PostDataViewModel;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.google.android.material.imageview.ShapeableImageView;

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
    private PostDataViewModel viewModel;
    private LifecycleOwner lifecycleOwner;
    private PostFragment postFragment;

    private void initOnClick(PostSessionHandler sessionHandler, Post post) {
        like.initLikeView(lifecycleOwner, (MutableLiveData<Boolean>) viewModel.getLikeLiveData());
        like.setLikeHelper(new LikeHelper() {
            @Override
            public MutableLiveData<Boolean> getLikeSync() {
                return sessionHandler.getLikeSync();
            }

            @Override
            public LiveData<String> doLike() {
                return sessionHandler.doLike();
            }

            @Override
            public LiveData<String> doUnLike() {
                return sessionHandler.doUnLike();
            }
        });

        HomePage activity = (HomePage) getContext();
        LiveData<Integer> commentId = viewModel.getViewCommentSessionId();
        commentId.observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                comment.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle args = new Bundle();
                        args.putInt("session id", integer);
                        activity.openCommentFragment(args, viewModel.getTotalCountLike());
                    }
                });
            }
        });
        avatarButton.setOnClickListener(view -> activity.openViewProfileFragment(post.getAuthor()));
    }

    private void initImagePostOnClick(ImagePost postItem, Integer postSessionId) {
        View imageView = mediaContainer.findViewWithTag("image");
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
                Bundle args = new Bundle();
                args.putInt("session id", postSessionId);
                activity.openViewImageFragment(args, bound, postItem.getImage());
            }
        });

    }

    private void initUserInfoContent(Post post) {
        Bitmap avatar = post.getAuthor().getAvatar();
        avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), avatar), 0);
        UserBasicInfo author = post.getAuthor();
        fullnameTextView.setText(author.getFullname());
    }

    private ImageView initImageView(ImagePost postItem) {
        ImageView imageView = new ImageView(getContext());
        BitmapDrawable drawable = new BitmapDrawable(getResources(), postItem.getImage());
        imageView.setImageDrawable(drawable);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
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

    private void initMediaContent(Post post) {
        String type = post.getType();
        if (type.equals("text")) {
            mainContentPanel.removeView(mediaContainer);
            mediaContainer = null;
            return;
        }
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        View mediaView = null;
        if (type.equals("image")) {
            mediaView = initImageView((ImagePost) post);
        } else if (post.getType().equals("video")) {
            mediaView = initMediaPlayer((MediaPost) post);
        }
        mediaView.setTag(type);
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

    private void initStatusContent(Post post) {
        String status = post.getStatus();
        if (status != null) {
            postContentTextVIew.setStatusContent(status);
        } else {
            mainContentPanel.removeView(postContentTextVIew);
            postContentTextVIew = null;
        }
    }

    private void initViewData(Post post) {
        initStatusContent(post);
        initUserInfoContent(post);
        initMediaContent(post);
    }

    public PostDataViewModel getViewModel() {
        return viewModel;
    }

    public PostItemView(PostFragment postFragment) {
        super(postFragment.getContext());
        this.postFragment = postFragment;

        lifecycleOwner = postFragment.getViewLifecycleOwner();
        LayoutInflater inflater = LayoutInflater.from(postFragment.getContext());
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

    }


    public void initViewModel(Post post) {
        initViewData(post);

        HomePage homePage = (HomePage) getContext();
        UserSessionViewModel userSessionViewModel = homePage.getViewModel();
        LiveData<String> userHost = userSessionViewModel.getFullname();

        PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
        LiveData<SessionHandler> postSessionHandler = postFragmentViewModel.createPostSession(post);

        viewModel = new PostDataViewModel(postSessionHandler, userHost);
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
        viewModel.getTime().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                countTime.setText(s);
            }
        });
        postSessionHandler.observe(lifecycleOwner, new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                initOnClick((PostSessionHandler) sessionHandler, post);
            }
        });
        if (post.getType().equals("image")) {
            LiveData<Integer> postSessionId = viewModel.getPostSessionId();
            postSessionId.observe(lifecycleOwner, new Observer<Integer>() {
                @Override
                public void onChanged(Integer integer) {
                    initImagePostOnClick((ImagePost) post, integer);
                }
            });
        }
    }
}
