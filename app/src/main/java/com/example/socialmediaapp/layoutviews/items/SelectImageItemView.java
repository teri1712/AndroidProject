package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.home.fragment.message.ImageSelectorFragment;
import com.squareup.picasso.Picasso;

public class SelectImageItemView extends FrameLayout {
   private TextView number;
   private View mask;
   private ImageView imageView;
   private boolean inited;

   public SelectImageItemView(Context context) {
      super(context);
      LayoutInflater inflater = LayoutInflater.from(getContext());
      inflater.inflate(R.layout.item_selected_image, this, true);
      number = findViewById(R.id.selected_number);
      mask = findViewById(R.id.selected_mask);
      imageView = findViewById(R.id.image_view);
      inited = false;
   }

   public void initViewModel(
           ImageSelectorFragment.ImageSelectManager selectManager,
           int order) {
      setValue(selectManager.findSelectedOrder(order));
      if (!inited) {
         Uri uri = selectManager.getUri(order);
         int w = getResources().getDisplayMetrics().widthPixels;
         Picasso.get().load(uri)
                 .resize(w / 4, w / 4)
                 .centerCrop()
                 .into(imageView);
      }
      setClickable(true);
      setOnClickListener(view -> {
         selectManager.onClick(order);
      });
      inited = true;
   }

   public void setValue(Integer integer) {
      if (integer == null) {
         mask.setVisibility(GONE);
      } else {
         mask.setVisibility(VISIBLE);
         number.setText(Integer.toString(integer + 1));
      }
   }
}
