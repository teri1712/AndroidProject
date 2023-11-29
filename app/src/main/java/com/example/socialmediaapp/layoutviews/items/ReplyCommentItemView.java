package com.example.socialmediaapp.layoutviews.items;

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
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.textview.LikeTextView;
import com.example.socialmediaapp.services.ServiceApi;
import com.example.socialmediaapp.viewmodel.items.ReplyCommentItemViewModel;
import com.example.socialmediaapp.viewmodel.models.post.ReplyComment;

public class ReplyCommentItemView extends ClickablePanel {

    private ReplyCommentItemViewModel viewModel;

    protected ViewGroup root;
    private CircleButton avatarButton;
    private TextView fullname, content;
    private ImageView imageContent;
    private TextView cntTime, cntLike;
    private LikeTextView likeTextView;
    protected View mainContentPanel;
    private FrameLayout backgroundPanel;
//    private ItemRepository<ReplyComment> repo;
    private LifecycleOwner lifecycleOwner;

    public ReplyCommentItemView(Fragment owner, int id) {
        super(owner.getContext());
//        this.repo = repo;
        lifecycleOwner = owner.getViewLifecycleOwner();
//        viewModel = new ReplyCommentItemViewModel(repo.getItem(id));
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
        initContent();
        initOnClick();
    }

    private void initContent() {
        ReplyComment replyComment = viewModel.getReplyComment();

        avatarButton.setBackgroundContent(null, 0);
        fullname.setText(replyComment.getSender().getFullname());
        if (replyComment.getContent() != null && !replyComment.getContent().isEmpty()) {
            content.setText(replyComment.getContent());
            content.setVisibility(VISIBLE);
            backgroundPanel.setWillNotDraw(false);
        }
        if (replyComment.getImage() != null) {
            imageContent.setImageDrawable(replyComment.getImage());
            imageContent.setVisibility(VISIBLE);
        }

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
        likeTextView.setClickAction(new LikeTextView.Action() {
            @Override
            public MutableLiveData<String> activeAction(boolean isActive) {
                MutableLiveData<String> res = new MutableLiveData<>();
//                if (isActive) {
//                    ServiceApi.likeReplyComment(replyComment.getId(), res);
//                } else {
//                    ServiceApi.unlikeReplyComment(replyComment.getId(), res);
//                }
                return res;
            }
        });
    }

    private void initOnClick() {

    }


}
