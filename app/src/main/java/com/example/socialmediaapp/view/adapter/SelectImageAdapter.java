package com.example.socialmediaapp.view.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.message.ImageSelectorFragment;
import com.example.socialmediaapp.layoutviews.items.SelectImageItemView;
import com.example.socialmediaapp.view.layout.CommentGroupManager;

import java.util.List;

public class SelectImageAdapter extends RecyclerView.Adapter<SelectImageAdapter.ItemHolder> {
   private ImageSelectorFragment.ImageSelectManager selectManager;

   public SelectImageAdapter(ImageSelectorFragment.ImageSelectManager selectManager) {
      this.selectManager = selectManager;
   }

   @Override
   public SelectImageAdapter.ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      SelectImageItemView view = new SelectImageItemView(parent.getContext());
      int w = parent.getContext().getResources().getDisplayMetrics().widthPixels;
      view.setLayoutParams(new ViewGroup.LayoutParams(w / 4, w / 4));
      return new ItemHolder(view);
   }

   @Override
   public void onBindViewHolder(ItemHolder holder, int pos) {
      holder.selectImageItemView.initViewModel(selectManager, pos);
   }

   @Override
   public int getItemCount() {
      return selectManager.length();
   }

   public class ItemHolder extends RecyclerView.ViewHolder {
      private SelectImageItemView selectImageItemView;

      public ItemHolder(SelectImageItemView itemView) {
         super(itemView);
         this.selectImageItemView = itemView;
      }
   }
}