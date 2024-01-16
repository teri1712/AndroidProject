package com.example.socialmediaapp.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.activity.MessageHome;
import com.example.socialmediaapp.layoutviews.items.OnlineUserItemView;
import com.example.socialmediaapp.models.messenger.OnlineUserItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UserOnlineAdapter extends RecyclerView.Adapter<UserOnlineAdapter.ItemHolder> {
   private Map<String, OnlineUserItemView> viewCaches;
   private List<OnlineUserItem> onlineUserItems;
   private MessageHome messageHome;

   public UserOnlineAdapter(MessageHome messageHome, List<OnlineUserItem> onlineUserItems) {
      this.messageHome = messageHome;
      this.onlineUserItems = onlineUserItems;
      viewCaches = new HashMap<>();
   }

   @Override
   public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      View v = new FrameLayout(parent.getContext());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      v.setLayoutParams(params);
      ItemHolder vh = new ItemHolder(v);
      return vh;
   }

   @Override
   public void onBindViewHolder(ItemHolder holder, int pos) {
      ViewGroup proxyHolder = (ViewGroup) holder.itemView;
      proxyHolder.removeAllViews();

      View item;
      OnlineUserItem onlineUserItem = onlineUserItems.get(pos);
      OnlineUserItemView onlineUserItemView = viewCaches.get(onlineUserItem.getChatInfo().getOther());
      System.out.println("+++" + pos + onlineUserItem.getChatInfo().getOther());

      if (onlineUserItemView == null) {
         onlineUserItemView = new OnlineUserItemView(messageHome);
         onlineUserItemView.initViewModel(onlineUserItem);
         viewCaches.put(onlineUserItem.getChatInfo().getOther(), onlineUserItemView);
      }
      item = onlineUserItemView;
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
      return onlineUserItems.size();
   }

   public class ItemHolder extends RecyclerView.ViewHolder {
      public ItemHolder(View itemView) {
         super(itemView);
      }
   }
}
