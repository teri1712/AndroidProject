package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.container.session.CommentSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.textview.LikeTextView;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.items.CommentItemViewModel;
import com.example.socialmediaapp.viewmodel.models.repo.ItemRepository;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.refactor.CommentDataViewModel;

public class CommentItemView extends ClickablePanel {

    private CommentDataViewModel viewModel;

    protected ViewGroup root;
    private CircleButton avatarButton;
    private TextView fullname, content;
    private ImageView imageContent;
    private TextView replyTextView, cntTime, cntLike;
    private LikeTextView likeTextView;
    private FrameLayout backgroundPanel;
    private LifecycleOwner lifecycleOwner;
    private ViewGroup replyPanel, infoPanel, mainContentPanel;
    private MutableLiveData<String> sessionState;
    private SessionHandler.SessionRegistry sessionRegistry;
    private CommentSessionHandler commentSessionHandler;

    public CommentItemView(Fragment owner, CommentDataViewModel commentDataViewModel) {
        super(owner.getContext());
        lifecycleOwner = owner.getViewLifecycleOwner();
        viewModel = commentDataViewModel;
        commentSessionHandler = viewModel.getCommentSessionHandler();
        setFocusable(false);
        setFocusableInTouchMode(false);
        LayoutInflater inflater = LayoutInflater.from(owner.getContext());
        root = (ViewGroup) inflater.inflate(R.layout.comment_item, this, false);

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
        replyPanel = root.findViewById(R.id.reply_panel);
        mainContentPanel = root.findViewById(R.id.main_content_panel);
        backgroundPanel = root.findViewById(R.id.background_panel);
        infoPanel = root.findViewById(R.id.information_panel);
        initContent();

        sessionRegistry = viewModel.getSessionRegistry();
        sessionState = viewModel.getSessionState();
        sessionState.observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("success")) {
                    initOnClick();
                }
            }
        });
    }

    private void initContent() {
        viewModel.getLiveData().observe(lifecycleOwner, new Observer<Comment>() {
            @Override
            public void onChanged(Comment comment) {
                Bitmap avatar = comment.getAuthor().getAvatar();
                avatarButton.setBackgroundContent(avatar == null ? null : new BitmapDrawable(getResources(), avatar), 0);
                fullname.setText(comment.getAuthor().getFullname());
                if (comment.getContent() != null && !comment.getContent().isEmpty()) {
                    content.setText(comment.getContent());
                    content.setVisibility(VISIBLE);
                    backgroundPanel.setWillNotDraw(false);
                }
                if (comment.getImage() != null) {
                    imageContent.setImageDrawable(comment.getImage());
                    imageContent.setVisibility(VISIBLE);
                }
            }
        });

        viewModel.getTime().observe(lifecycleOwner, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                cntTime.setText(s);
            }
        });

        viewModel.getCountLike().observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    cntLike.setVisibility(GONE);
                } else {
                    if (cntLike.getVisibility() == GONE) cntLike.setVisibility(VISIBLE);
                    cntLike.setText(Integer.toString(integer));
                }
            }
        });
        likeTextView.initLikeView(lifecycleOwner, viewModel.getIsLiked());
    }

    private void initOnClick() {
        likeTextView.setClickAction(new LikeTextView.Action() {
            @Override
            public MutableLiveData<String> activeAction(boolean isActive) {
                return isActive ? commentSessionHandler.doLike() : commentSessionHandler.doUnLike();
            }
        });
    }

    private int drawChildBranch(Canvas canvas, Paint color, View child, int x, int y) {
        Rect rect = new Rect(0, 0, child.getWidth(), child.getHeight());
        replyPanel.offsetDescendantRectToMyCoords(child, rect);
        infoPanel.offsetDescendantRectToMyCoords(replyPanel, rect);
        mainContentPanel.offsetDescendantRectToMyCoords(infoPanel, rect);
        root.offsetDescendantRectToMyCoords(mainContentPanel, rect);

        int targetY = (rect.top + rect.bottom) / 2;
        int targetX = rect.left;

        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, targetY - 10);
        path.quadTo(x, targetY, x + 10, targetY);
        path.lineTo(targetX - 5, targetY);
        path.close();
        canvas.drawPath(path, color);
        return targetY - 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#e9ecef"));
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        Rect rect = new Rect(0, 0, avatarButton.getWidth(), avatarButton.getHeight());
        mainContentPanel.offsetDescendantRectToMyCoords(avatarButton, rect);
        root.offsetDescendantRectToMyCoords(mainContentPanel, rect);
        int x = (rect.left + rect.right) / 2, y = rect.bottom + 10;
        for (int i = 0; i < replyPanel.getChildCount(); i++) {
            y = drawChildBranch(canvas, paint, replyPanel.getChildAt(i), x, y);
        }
        super.onDraw(canvas);
    }

}
