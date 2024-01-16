package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.view.action.LikeHelper;

public class LikeButton extends PostButton {
   private LikeHelper likeHelper;
   private boolean white = false;

   public void setWhite(boolean white) {
      this.white = white;
   }

   public void initLikeView(
           LifecycleOwner lifecycleOwner,
           LiveData<Boolean> like) {
      like.observe(lifecycleOwner, isActive -> {
         if (!isActive) {
            setBackgroundContent(getResources().getDrawable(white ? R.drawable.white_like_24 : R.drawable.like_24, null));
            setTextContentColor(white ? Color.WHITE : Color.parseColor("#757575"));

         } else {
            setBackgroundContent(getResources().getDrawable(R.drawable.active_like_24, null));
            setTextContentColor(Color.parseColor("#0866FF"));
         }
      });
      setOnClickListener(view -> {
         boolean liked = like.getValue();
         if (liked) {
            likeHelper.unlike();
         } else {
            likeHelper.like();
         }
      });
   }
   public void setLikeHelper(LikeHelper likeHelper) {
      this.likeHelper = likeHelper;
   }

   public LikeButton(@NonNull Context context) {
      super(context);
   }

   public LikeButton(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public LikeButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }
}
