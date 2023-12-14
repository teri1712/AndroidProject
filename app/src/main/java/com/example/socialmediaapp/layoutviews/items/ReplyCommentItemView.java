package com.example.socialmediaapp.layoutviews.items;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.ReplyCommentSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.textview.LikeTextView;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.CommentDataViewModel;
import com.example.socialmediaapp.viewmodel.ReplyCommentDataViewModel;
import com.example.socialmediaapp.viewmodel.dunno.LikeHelper;
import com.example.socialmediaapp.viewmodel.items.ReplyCommentItemViewModel;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;

public class ReplyCommentItemView extends FrameLayout {

    private ReplyCommentDataViewModel viewModel;
    private ViewGroup root;
    private CircleButton avatarButton;
    private TextView fullname, content;
    private ImageView imageContent;
    private TextView cntTime, cntLike;
    private LikeTextView likeTextView;
    private ViewGroup mainContentPanel;
    private FrameLayout backgroundPanel;
    private LifecycleOwner lifecycleOwner;

    public ReplyCommentItemView(Fragment owner, LiveData<SessionHandler> commentSessionHandler, ReplyComment comment) {
        super(owner.getContext());
        lifecycleOwner = owner.getViewLifecycleOwner();
        setFocusable(false);
        setFocusableInTouchMode(false);
        LayoutInflater inflater = LayoutInflater.from(owner.getContext());
        root = (ViewGroup) inflater.inflate(R.layout.reply_comment_item, this, false);

        addView(root);
        fullname = root.findViewById(R.id.fullname);
        avatarButton = root.findViewById(R.id.avatar_button);
        content = root.findViewById(R.id.comment_content);
        imageContent = root.findViewById(R.id.image_view);
        likeTextView = root.findViewById(R.id.like_text_view);
        cntTime = root.findViewById(R.id.cnt_time);
        cntLike = root.findViewById(R.id.cnt_like);
        imageContent = root.findViewById(R.id.image_view);
        mainContentPanel = root.findViewById(R.id.main_content_panel);
        backgroundPanel = root.findViewById(R.id.background_panel);
        initContent(comment);
        initViewModel(commentSessionHandler, comment);
        initOnClick(commentSessionHandler, comment);
    }

    private void initViewModel(LiveData<SessionHandler> commentSessionHandler, ReplyComment comment) {
        viewModel = new ReplyCommentDataViewModel(commentSessionHandler, comment);
        viewModel.getTime().observe(lifecycleOwner, s -> cntTime.setText(s));
        viewModel.getCountLikeContent().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.isEmpty()) {
                    cntLike.setVisibility(GONE);
                } else {
                    cntLike.setText(s);
                    cntLike.setVisibility(VISIBLE);
                }
            }
        });
        commentSessionHandler.observe(lifecycleOwner, new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                likeTextView.initLikeView(lifecycleOwner, viewModel.getLikeLiveData());
            }
        });
    }

    private void initContent(ReplyComment comment) {
        Bitmap avatar = comment.getSender().getAvatar();
        avatarButton.setBackgroundContent(avatar == null ? null : new BitmapDrawable(getResources(), avatar), 0);
        fullname.setText(comment.getSender().getFullname());
        if (comment.getContent() != null && !comment.getContent().isEmpty()) {
            content.setText(comment.getContent());
            content.setVisibility(VISIBLE);
            backgroundPanel.setWillNotDraw(false);
        }
        if (comment.getImage() != null) {
            imageContent.setImageDrawable(new BitmapDrawable(getResources(), comment.getImage()));
            imageContent.setVisibility(VISIBLE);
        }
    }

    private void initOnClick(LiveData<SessionHandler> sessionHandlerLiveData, ReplyComment replyComment) {

        sessionHandlerLiveData.observe(lifecycleOwner, sessionHandler -> {
            ReplyCommentSessionHandler commentSessionHandler = (ReplyCommentSessionHandler) sessionHandler;
            likeTextView.setLikeHelper(new LikeHelper() {
                @Override
                public MutableLiveData<Boolean> getLikeSync() {
                    return commentSessionHandler.getLikeSync();
                }

                @Override
                public LiveData<String> doLike() {
                    return commentSessionHandler.doLike();
                }

                @Override
                public LiveData<String> doUnLike() {
                    return commentSessionHandler.doUnLike();
                }
            });
        });
        avatarButton.setOnClickListener(view -> {
            HomePage activity = (HomePage) getContext();
            activity.openViewProfileFragment(replyComment.getSender());
        });
    }

    public void offsetOfAvatar(Rect rect) {
        rect.bottom = avatarButton.getHeight();
        mainContentPanel.offsetDescendantRectToMyCoords(avatarButton, rect);
        root.offsetDescendantRectToMyCoords(mainContentPanel, rect);
    }

}
