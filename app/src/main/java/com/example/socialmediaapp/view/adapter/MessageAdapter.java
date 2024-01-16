package com.example.socialmediaapp.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.message.MessageFragment;
import com.example.socialmediaapp.layoutviews.items.message.RightMessageItemView;
import com.example.socialmediaapp.layoutviews.items.message.icon.LeftIconItemView;
import com.example.socialmediaapp.layoutviews.items.message.icon.RightLikeIconItemView;
import com.example.socialmediaapp.layoutviews.items.message.layout.MessageGroupLayoutManager;
import com.example.socialmediaapp.layoutviews.items.message.MessageItemView;
import com.example.socialmediaapp.layoutviews.items.message.LeftMessageItemView;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;
import com.example.socialmediaapp.application.repo.core.MessageRepository;

import java.util.List;
import java.util.TreeMap;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemHolder> {
   private MessageRepository repository;
   private MessageFragment messageFragment;
   private TreeMap<Long, MessageItemView> viewCaches;
   private List<RecyclerViewExtra> topViews;
   private List<RecyclerViewExtra> endViews;
   private MessageGroupLayoutManager messageGroupLayoutManager = new MessageGroupLayoutManager();

   public MessageAdapter(MessageFragment messageFragment, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
      this.messageFragment = messageFragment;
      this.topViews = topViews;
      this.endViews = endViews;
      viewCaches = new TreeMap<>();
      repository = messageFragment.getViewModel().getRepo();
   }

   @Override
   public MessageAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
         MessageItemModel messageItemModel = repository.get(pos - topViews.size());
         MessageItemView messageItemView = viewCaches.get(messageItemModel.getTime());
         if (messageItemView == null) {

            boolean isMine = messageItemModel.isMine();
            if (messageItemModel.getType().equals("icon")) {
               messageItemView = isMine ? new RightLikeIconItemView(messageFragment) : new LeftIconItemView(messageFragment);
            } else {
               messageItemView = isMine ? new RightMessageItemView(messageFragment) : new LeftMessageItemView(messageFragment);
            }
            messageItemView.initViewModel(messageFragment.getViewModel().getChatSessionModel(), messageItemModel);
            viewCaches.put(messageItemModel.getTime(), messageItemView);

            MessageGroupLayoutManager.MessageViewSource messageViewSource = new MessageGroupLayoutManager.MessageViewSource(messageItemModel, messageItemView);
            messageGroupLayoutManager.applyNewItem(messageViewSource, pos - topViews.size());
         }
         item = messageItemView;

      } else {
         RecyclerViewExtra extra = pos < topViews.size()
                 ? topViews.get(pos)
                 : endViews.get(pos - topViews.size() - repository.length());
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

   public void notifyViewRangeInserted(int positionStart, int itemCount) {
      notifyItemRangeInserted(positionStart + topViews.size(), itemCount);
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