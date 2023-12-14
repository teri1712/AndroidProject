package com.example.socialmediaapp.viewmodel.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;

import java.util.List;
import java.util.TreeMap;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ItemHolder> {
    private Repository<Post> repository;
    private PostFragment postFragment;
    private List<RecyclerViewExtra> topViews, endViews;
    private TreeMap<Integer, PostItemView> viewCaches;

    public PostAdapter(PostFragment postFragment, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
        this.postFragment = postFragment;
        this.endViews = endViews;
        this.topViews = topViews;
        PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
        repository = postFragmentViewModel.getPostRepository();
        viewCaches = new TreeMap<>();
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
            Post post = repository.get(pos - topViews.size());
            PostItemView postItemView = viewCaches.get(post.getId());
            if (postItemView == null) {
                postItemView = new PostItemView(postFragment);
                postItemView.initViewModel(post);
                viewCaches.put(post.getId(), postItemView);
            }
            item = postItemView;
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

    public void recycle() {
        viewCaches.clear();
    }

    @Override
    public int getItemCount() {
        return topViews.size() + endViews.size() + repository.length();
    }

    public class ItemHolder extends RecyclerView.ViewHolder {
        public ItemHolder(View itemView) {
            super(itemView);
        }
    }
}
