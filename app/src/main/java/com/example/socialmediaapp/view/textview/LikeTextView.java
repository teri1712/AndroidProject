package com.example.socialmediaapp.view.textview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.view.action.LikeHelper;

public class LikeTextView extends ClickableTextView {
   private String actionState;
   private LikeHelper likeHelper;

   public void initLikeView(
           LifecycleOwner lifecycleOwner,
           LiveData<Boolean> likeLiveData) {
      actionState = "Idle";
      likeLiveData.observe(lifecycleOwner, isActive -> {
         if (isActive == null) return;
         if (!isActive) {
            setTextColor(Color.parseColor("#757575"));
         } else {
            setTextColor(Color.parseColor("#0866FF"));
         }
      });
      setOnClickListener(view -> {
         boolean liked = likeLiveData.getValue();
         if (liked) {
            likeHelper.unlike();
         }else{
            likeHelper.like();
         }
      });

   }

   public void setLikeHelper(LikeHelper likeHelper) {
      this.likeHelper = likeHelper;
   }
   public LikeTextView(@NonNull Context context) {
      super(context);
   }

   public LikeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public LikeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public interface Action {
      MutableLiveData<String> activeAction(boolean isActive);
   }
}
