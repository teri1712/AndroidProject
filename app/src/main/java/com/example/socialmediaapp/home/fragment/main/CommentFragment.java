package com.example.socialmediaapp.home.fragment.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.customview.progress.CommentLoading;
import com.example.socialmediaapp.layoutviews.items.CommentItemView;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

public class CommentFragment extends Fragment {


    public CommentFragment(DataAccessHandler<Comment> handler) {
        this.handler = handler;
    }

    private DataAccessHandler<Comment> handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new CommentFragmentViewModel(handler);
    }

    private ViewGroup commentPanel;
    private CommentFragmentViewModel viewModel;
    private CommentLoading commentLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment, container, false);

        commentPanel = view.findViewById(R.id.comment_panel);
        commentLoading = new CommentLoading(getContext());


        initViewModel();
        return view;
    }

    public CommentFragmentViewModel getViewModel() {
        return viewModel;
    }

    public void initViewModel() {
        viewModel.getLoadCommentState().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                performLoading();
            } else {
                finishLoading();
            }
        });
        viewModel.getCommentUpdate().observe(getViewLifecycleOwner(), new Observer<Update<Comment>>() {
            @Override
            public void onChanged(Update<Comment> commentUpdate) {
                Update.Op op = commentUpdate.getOp();
                int pos = commentUpdate.getPos();
                Comment comment = commentUpdate.getItem();
                if (op == Update.Op.ADD) {
                    CommentItemView commentItemView = new CommentItemView(CommentFragment.this, viewModel.createCommentSession(comment), comment);
                    if (pos == -1) {
                        commentPanel.addView(commentItemView);
                    } else {
                        commentPanel.addView(commentItemView, 0);
                    }
                } else {
                    commentPanel.removeViewAt(pos);
                }
            }
        });
        viewModel.loadComments();
    }

    private void performLoading() {
        commentLoading.start();
        commentPanel.addView(commentLoading);
    }

    private void finishLoading() {
        commentLoading.cancel();
        commentPanel.removeView(commentLoading);
    }

    public void onDestroyView() {
        super.onDestroyView();
        commentLoading.cancel();
    }

}
