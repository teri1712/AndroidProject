package com.example.socialmediaapp.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.home.fragment.extras.ExtraViewHolder;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.application.repo.core.Repository;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ItemHolder> {
  private static final int EXTRA_VIEW = 1, POST_VIEW = 2;
  private Repository<HandlerAccess> repo;
  private ExtraViewHolder topViews, endViews;

  public PostAdapter(Repository<HandlerAccess> repo) {
    this.repo = repo;
  }

  public void applyExtraViews(ExtraViewHolder topViews, ExtraViewHolder endViews) {
    this.topViews = topViews;
    this.endViews = endViews;
    topViews.setAdapter(this);
    endViews.setAdapter(this);
  }

  @Override
  public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = null;
    switch (viewType) {
      case POST_VIEW:
        v = new PostItemView(parent.getContext());
        break;
      case EXTRA_VIEW:
        v = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
        break;
      default:
        break;
    }
    ItemHolder vh = new ItemHolder(v);
    return vh;
  }

  @Override
  public int getItemViewType(int pos) {
    if (pos >= topViews.length() && pos < topViews.length() + repo.length()) {
      return POST_VIEW;
    }
    return EXTRA_VIEW;
  }

  @Override
  public void onBindViewHolder(ItemHolder holder, int pos) {
    int len = repo.length();
    int aPos = pos - topViews.length();

    if (aPos >= 0 && aPos < len) {
      HandlerAccess handlerAccess = repo.get(aPos);
      PostItemView postView = (PostItemView) holder.itemView;
      postView.initViewModel(handlerAccess);
    } else {
      ViewGroup proxyHolder = (ViewGroup) holder.itemView;
      proxyHolder.removeAllViews();
      RecyclerViewExtra extra = aPos < 0
              ? topViews.getAt(pos)
              : endViews.getAt(aPos - len);
      View item = extra.getView();
      if (!extra.isConfigured()) {
        extra.configure(item);
        extra.setConfigured(true);
      }
      ViewGroup preParentOfView = (ViewGroup) item.getParent();
      if (preParentOfView != null)
        preParentOfView.removeView(item);
      proxyHolder.addView(item);
    }
  }

  public void notifyViewRangeInserted(int positionStart, int itemCount) {
    notifyItemRangeInserted(positionStart + topViews.length(), itemCount);
  }

  @Override
  public int getItemCount() {
    return topViews.length() + endViews.length() + repo.length();
  }

  public class ItemHolder extends RecyclerView.ViewHolder {
    public ItemHolder(View itemView) {
      super(itemView);
    }
  }
}
