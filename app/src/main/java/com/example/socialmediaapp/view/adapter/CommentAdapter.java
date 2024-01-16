package com.example.socialmediaapp.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.home.fragment.extras.ExtraViewHolder;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.layoutviews.items.CommentItemView;
import com.example.socialmediaapp.layoutviews.items.ReplyCommentItemView;
import com.example.socialmediaapp.layoutviews.items.ViewReplyItem;
import com.example.socialmediaapp.view.layout.CommentGroupManager;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ItemHolder> {
  public static final int COMMENT_VIEW = 1, REPLY_VIEW = 2, LOAD_VIEW = 3, EXTRA_VIEW = 4;
  private CommentGroupManager commentManager;
  private ExtraViewHolder topViews, endViews;

  public CommentAdapter(CommentGroupManager commentManager) {
    this.commentManager = commentManager;
  }


  public void applyExtraViews(ExtraViewHolder topViews, ExtraViewHolder endViews) {
    this.topViews = topViews;
    this.endViews = endViews;
    topViews.setAdapter(this);
    endViews.setAdapter(this);
  }

  @Override
  public int getItemViewType(int pos) {
    if (pos >= topViews.length() && pos < topViews.length() + commentManager.length()) {
      return commentManager.getViewType(pos - topViews.length());
    }
    return EXTRA_VIEW;
  }

  @Override
  public CommentAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View v = null;
    switch (viewType) {
      case EXTRA_VIEW:
        v = new FrameLayout(parent.getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(params);
        break;
      case COMMENT_VIEW:
        v = new CommentItemView(parent.getContext());
        break;
      case REPLY_VIEW:
        v = new ReplyCommentItemView(parent.getContext());
        break;
      case LOAD_VIEW:
        v = new ViewReplyItem(parent.getContext());
        break;
      default:
        break;
    }
    ItemHolder vh = new ItemHolder(v);
    return vh;
  }

  @Override
  public void onBindViewHolder(ItemHolder holder, int pos) {
    int len = commentManager.length();
    int aPos = pos - topViews.length();
    if (aPos >= 0 && aPos < len) {
      commentManager.applyViewModel(aPos, holder.itemView);
    } else {
      RecyclerViewExtra extra = aPos < 0
              ? topViews.getAt(pos)
              : endViews.getAt(aPos - len);
      ViewGroup proxyHolder = (ViewGroup) holder.itemView;
      proxyHolder.removeAllViews();

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

  public void notifyViewChanged(int positionStart, int itemCount) {
    notifyItemChanged(positionStart + topViews.length(), itemCount);
  }


  @Override
  public int getItemCount() {
    return topViews.length() + commentManager.length() + endViews.length();
  }

  public class ItemHolder extends RecyclerView.ViewHolder {
    public ItemHolder(View itemView) {
      super(itemView);
    }
  }
}