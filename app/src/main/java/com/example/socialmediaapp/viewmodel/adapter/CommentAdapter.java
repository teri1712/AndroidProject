package com.example.socialmediaapp.viewmodel.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.main.CommentFragment;
import com.example.socialmediaapp.layoutviews.items.CommentItemView;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;

import java.util.List;
import java.util.TreeMap;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ItemHolder> {
    private Repository<Comment> repository;
    private CommentFragment commentFragment;
    private List<RecyclerViewExtra> topViews, endViews;
    private TreeMap<Integer, CommentItemView> viewCaches;

    public CommentAdapter(CommentFragment commentFragment, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
        this.commentFragment = commentFragment;
        this.topViews = topViews;
        this.endViews = endViews;

        CommentFragmentViewModel commentFragmentViewModel = commentFragment.getViewModel();
        repository = commentFragmentViewModel.getCommentRepository();
        viewCaches = new TreeMap<>();
    }

    @Override
    public CommentAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
        ItemHolder vh = new ItemHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int pos) {
        ViewGroup proxyHolder = (ViewGroup) holder.itemView;
        proxyHolder.removeAllViews();

        View item;
        if (pos >= topViews.size() && pos < topViews.size() + repository.length()) {
            Comment comment = repository.get(pos - topViews.size());
            CommentItemView commentItemView = viewCaches.get(comment.getId());
            if (commentItemView == null) {
                commentItemView = new CommentItemView(commentFragment);
                commentItemView.initViewModel(comment);
                viewCaches.put(comment.getId(), commentItemView);
            }
            item = commentItemView;
        } else {
            RecyclerViewExtra extra = pos < topViews.size() ? topViews.get(pos) : endViews.get(pos - topViews.size() - repository.length());
            item = extra.getView();
            if (!extra.isConfigured()) {
                extra.configure(item);
                extra.setConfigured(true);
            }
        }
        ViewGroup preParentOfView = (ViewGroup) item.getParent();
        if (preParentOfView != null)
            preParentOfView.removeView(item);
        proxyHolder.addView(item);
    }

    @Override
    public int getItemCount() {
        return topViews.size() + repository.length() + endViews.size();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}