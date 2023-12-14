package com.example.socialmediaapp.viewmodel.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.main.MessageFragment;
import com.example.socialmediaapp.layoutviews.items.MessageGroupLayoutManager;
import com.example.socialmediaapp.layoutviews.items.MessageItemView;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.repo.MessageRepository;

import java.util.List;
import java.util.TreeMap;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ItemHolder> {
   private MessageRepository repository;
   private MessageFragment messageFragment;
   private TreeMap<Integer, MessageItemView> viewCaches;
   private List<RecyclerViewExtra> topViews;
   private List<RecyclerViewExtra> endViews;
   private MessageGroupLayoutManager messageGroupLayoutManager = new MessageGroupLayoutManager();

   public MessageAdapter(MessageFragment messageFragment, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
      this.messageFragment = messageFragment;
      this.topViews = topViews;
      this.endViews = endViews;
      viewCaches = new TreeMap<>();
      repository = messageFragment.getViewModel().getMessageRepository();
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

      if (pos >= topViews.size()) {
         MessageItem messageItem = repository.get(pos - topViews.size());
         MessageItemView messageItemView = viewCaches.get(messageItem.getOrder());

         if (messageItemView == null) {
            messageItemView = new MessageItemView(messageFragment);
            messageItemView.initViewModel(messageFragment.getViewModel().getChatSessionModel(), messageItem);
            viewCaches.put(messageItem.getOrder(), messageItemView);

            MessageGroupLayoutManager.MessageViewSource messageViewSource = new MessageGroupLayoutManager.MessageViewSource(messageItem.getTime(), messageItemView);
            messageGroupLayoutManager.applyNewItem(messageViewSource, pos - topViews.size());

         }
         item = messageItemView;

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
      return repository.length();
   }

   public class ItemHolder extends RecyclerView.ViewHolder {
      public ItemHolder(View itemView) {
         super(itemView);
      }
   }
}