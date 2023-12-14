package com.example.socialmediaapp.viewmodel.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.activitiy.MessageHome;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.layoutviews.items.ChatItemView;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;

import java.util.List;
import java.util.TreeMap;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ItemHolder> {
   private List<RecyclerViewExtra> topViews, endViews;
   private TreeMap<Integer, ChatItemView> viewCaches;
   private List<ChatSessionModel> chatSessionModelList;
   private Context context;

   public ChatAdapter(MessageHome messageHome, List<RecyclerViewExtra> topViews, List<RecyclerViewExtra> endViews) {
      this.chatSessionModelList = messageHome.getViewModel().getListChatSession();
      this.context = messageHome;
      this.endViews = endViews;
      this.topViews = topViews;
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
      if (pos >= topViews.size() && pos < topViews.size() + chatSessionModelList.size()) {
         ChatSessionModel chatSessionModel = chatSessionModelList.get(pos - topViews.size());

         ChatItemView chatItemView = viewCaches.get(chatSessionModel.getChatInfo().getChatId());
         if (chatItemView == null) {
            chatItemView = new ChatItemView(context);
            chatItemView.initViewModel(chatSessionModel);
            viewCaches.put(chatSessionModel.getChatInfo().getChatId(), chatItemView);
         }
         item = chatItemView;
      } else {
         RecyclerViewExtra extra = pos < topViews.size() ? topViews.get(pos) : endViews.get(pos - topViews.size() - chatSessionModelList.size());
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
      return topViews.size() + endViews.size() + chatSessionModelList.size();
   }

   public class ItemHolder extends RecyclerView.ViewHolder {
      public ItemHolder(View itemView) {
         super(itemView);
      }
   }
}
