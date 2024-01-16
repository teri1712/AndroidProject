package com.example.socialmediaapp.view.message;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class DragMessageLayout extends LinearLayout {

   private float preX, preY;
   private View msgView;
   private int msgViewRef;
   private void init(AttributeSet attrs) {
      TypedArray a = getContext().obtainStyledAttributes(
              attrs,
              R.styleable.my_container);
      try {
         msgViewRef = a.getResourceId(R.styleable.my_container_dragged_view_id, -1);
      } finally {
         a.recycle();
      }
   }

   @Override
   protected void onFinishInflate() {
      super.onFinishInflate();
      if (msgViewRef != -1) {
         msgView = findViewById(msgViewRef);
      }
   }

   public DragMessageLayout(Context context) {
      super(context);
   }

   public DragMessageLayout(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public DragMessageLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   public DragMessageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(attrs);
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      if (msgView == null) return false;
      float x = event.getX();
      float y = event.getY();
      if (Math.abs(x - preX) < 5 * Math.abs(y - preY)) {
         return false;
      }
      switch (event.getAction()) {
         case MotionEvent.ACTION_MOVE:
            float dif = x - preX;
            float transX = dif + msgView.getTranslationX();
            transX = Math.max(0, transX);
            msgView.setTranslationX(transX);
            break;
         case MotionEvent.ACTION_CANCEL:
         case MotionEvent.ACTION_UP:

            msgView.animate().setDuration(200).translationX(0).setInterpolator(new DecelerateInterpolator()).start();
            return false;
      }
      return true;
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent event) {
      if (msgView == null) return false;
      float x = event.getX();
      float y = event.getY();
      boolean willIntercept = false;
      switch (event.getAction()) {
         case MotionEvent.ACTION_MOVE:
            if (Math.abs(x - preX) > 5 * Math.abs(y - preY)) {
               willIntercept = true;
            }
            break;
         default:
            break;
      }
      preX = x;
      preY = y;
      return willIntercept;
   }

}
