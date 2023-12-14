package com.example.socialmediaapp.layoutviews.items;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
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
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.textview.ClickableTextView;
import com.example.socialmediaapp.customview.textview.LikeTextView;
import com.example.socialmediaapp.home.fragment.main.CommentFragment;
import com.example.socialmediaapp.home.fragment.main.MainCommentFragment;
import com.example.socialmediaapp.viewmodel.CommentDataViewModel;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.ReplyCommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.dunno.LikeHelper;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.HashMap;
import java.util.List;

public class CommentItemView extends FrameLayout {
    private CommentDataViewModel viewModel;
    private ViewGroup root;
    private CircleButton avatarButton;
    private TextView fullname, content;
    private ImageView imageContent;
    private TextView cntTime, cntLike;
    private ClickableTextView replyTextView;
    private LikeTextView likeTextView;
    private FrameLayout backgroundPanel;
    private LifecycleOwner lifecycleOwner;
    private ViewGroup replyPanel, infoPanel, mainContentPanel;
    private ClickablePanel viewMoreReply;
    private CommentFragment owner;
    private ReplyCommentFragmentViewModel replyCommentViewModel;
    private CustomSpinningView spinnerLoading;
    private TextView cntReplyTextView;
    private MediatorLiveData<Integer> countRemain = new MediatorLiveData<>();

    public CommentItemView(CommentFragment owner) {
        super(owner.getContext());
        this.owner = owner;
        lifecycleOwner = owner.getViewLifecycleOwner();

        setFocusable(false);
        setFocusableInTouchMode(false);
        setWillNotDraw(false);
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
        viewMoreReply = root.findViewById(R.id.view_more_reply_comment);
        spinnerLoading = root.findViewById(R.id.load_spinner);
        cntReplyTextView = root.findViewById(R.id.count_reply_textview);
    }

    private void initContent(Comment comment) {

        Bitmap avatar = comment.getAuthor().getAvatar();
        avatarButton.setBackgroundContent(avatar == null ? null : new BitmapDrawable(getResources(), avatar), 0);
        fullname.setText(comment.getAuthor().getFullname());

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

    private int calCountRemain() {
        Integer total = viewModel.getCountComment().getValue();
        Integer countLoaded = replyCommentViewModel.getCountLoaded().getValue();
        return (total == null ? 0 : total) - (countLoaded == null ? 0 : countLoaded);
    }

    public void initViewModel(Comment comment) {
        CommentFragmentViewModel commentFragmentViewModel = owner.getViewModel();
        LiveData<SessionHandler> sessionHandlerLiveData = commentFragmentViewModel.createCommentSession(comment);

        initContent(comment);

        viewModel = new CommentDataViewModel(sessionHandlerLiveData, comment);

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
        viewModel.getReplyCommentsSession().observe(lifecycleOwner, new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                initReplyList((DataAccessHandler<ReplyComment>) sessionHandler);
            }
        });
        viewModel.getCommentSessionHandler().observe(lifecycleOwner, new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                initOnClick((CommentSessionHandler) sessionHandler, comment);
                likeTextView.initLikeView(lifecycleOwner, viewModel.getLikeLiveData());
            }
        });
    }

    private void initReplyList(DataAccessHandler<ReplyComment> dataAccessHandler) {
        replyCommentViewModel = new ReplyCommentFragmentViewModel(dataAccessHandler);
        replyCommentViewModel.getLoadCommentState().observe(lifecycleOwner, aBoolean -> {
            if (aBoolean) {
                performLoading();
            } else {
                finishLoading();
            }
        });
        countRemain.addSource(viewModel.getCountComment(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                countRemain.setValue(calCountRemain());
            }
        });
        countRemain.addSource(replyCommentViewModel.getCountLoaded(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                countRemain.setValue(calCountRemain());
            }
        });
        countRemain.observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    viewMoreReply.setVisibility(GONE);
                } else {
                    viewMoreReply.setVisibility(VISIBLE);
                    cntReplyTextView.setText("View replies (" + integer + ")");
                }
            }
        });
        replyTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                MainCommentFragment mainCommentFragment = (MainCommentFragment) owner.getParentFragment();
                mainCommentFragment.setActionOnEditText(viewModel.getLiveData().getValue().getAuthor().getFullname(), replyCommentViewModel::uploadComment);
            }
        });
        viewMoreReply.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                replyCommentViewModel.load(5);
            }
        });
        viewModel.getCountComment().observe(lifecycleOwner, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer > 0) {
                    replyCommentViewModel.loadEntrance();
                }
                viewModel.getCountComment().removeObserver(this);
            }
        });

        Repository<ReplyComment> replyCommentRepository = replyCommentViewModel.getCommentRepository();
        LiveData<Update> replyCommentUpdate = replyCommentRepository.getItemUpdate();

        replyCommentUpdate.observe(lifecycleOwner, update -> {
            HashMap<String, Object> data = update.data;
            Integer offset = (Integer) data.get("offset");
            Integer length = (Integer) data.get("length");
            for (int i = 0; i < length; i++) {
                ReplyComment item = replyCommentRepository.get(offset + i);
                LiveData<SessionHandler> replySessionHandler = replyCommentViewModel.createReplyCommentSession(item);
                ReplyCommentItemView commentItemView = new ReplyCommentItemView(owner, replySessionHandler, item);
                replyPanel.addView(commentItemView, replyPanel.getChildCount() - 2);
            }
            MutableLiveData<Integer> countLoaded = replyCommentViewModel.getCountLoaded();
            Integer cnt = countLoaded.getValue();
            cnt += length;
            countLoaded.setValue(cnt);
        });

    }

    private void initOnClick(CommentSessionHandler commentSessionHandler, Comment comment) {
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
        avatarButton.setOnClickListener(view -> {
            HomePage activity = (HomePage) getContext();
            activity.openViewProfileFragment(comment.getAuthor());
        });
    }

    private void performLoading() {
        spinnerLoading.setVisibility(VISIBLE);
    }

    private void finishLoading() {
        spinnerLoading.setVisibility(GONE);
    }

    private int drawChildBranch(Canvas canvas, Paint color, View child, int x, int y) {
        Rect rect = new Rect(0, 0, 0, child.getHeight());
        if (child instanceof ReplyCommentItemView) {
            ((ReplyCommentItemView) child).offsetOfAvatar(rect);
        }
        replyPanel.offsetDescendantRectToMyCoords(child, rect);
        infoPanel.offsetDescendantRectToMyCoords(replyPanel, rect);
        mainContentPanel.offsetDescendantRectToMyCoords(infoPanel, rect);
        root.offsetDescendantRectToMyCoords(mainContentPanel, rect);

        int targetY = (rect.top + rect.bottom) / 2;
        int targetX = rect.left;

        Path path = new Path();
        path.moveTo(x, y);
        path.lineTo(x, targetY - 25);
        path.quadTo(x, targetY, x + 25, targetY);
        path.lineTo(targetX - 10, targetY);
        canvas.drawPath(path, color);
        return targetY - 25;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint treeStroke = new Paint();
        treeStroke.setColor(Color.parseColor("#dee2e6"));
        treeStroke.setAntiAlias(true);
        treeStroke.setStyle(Paint.Style.STROKE);
        treeStroke.setStrokeWidth(5);
        Rect rect = new Rect(0, 0, avatarButton.getWidth(), avatarButton.getHeight());
        mainContentPanel.offsetDescendantRectToMyCoords(avatarButton, rect);
        root.offsetDescendantRectToMyCoords(mainContentPanel, rect);
        int x = (rect.left + rect.right) / 2, y = rect.bottom + 15;
        for (int i = 0; i < replyPanel.getChildCount(); i++) {
            View v = replyPanel.getChildAt(i);
            if (v.getVisibility() == GONE || v == spinnerLoading) continue;
            y = drawChildBranch(canvas, treeStroke, v, x, y);
        }
        super.onDraw(canvas);
    }
}
