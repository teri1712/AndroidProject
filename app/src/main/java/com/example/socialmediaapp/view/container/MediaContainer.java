package com.example.socialmediaapp.view.container;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MediaContainer extends ClickablePanel {
   public MediaContainer(@NonNull Context context) {
      super(context);
   }

   public MediaContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public MediaContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public MediaContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
   }

   @Override
   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
      super.onMeasure(widthMeasureSpec, heightMeasureSpec);

      int w = MeasureSpec.getSize(widthMeasureSpec);
      int mode = MeasureSpec.getMode(heightMeasureSpec);
      int h = MeasureSpec.getSize(heightMeasureSpec);
      int max_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400, getContext().getResources().getDisplayMetrics());

      switch (mode) {
         case MeasureSpec.AT_MOST:
            h = Math.min(h, max_height);
            break;
         case MeasureSpec.EXACTLY:
            break;
         case MeasureSpec.UNSPECIFIED:
            h = max_height;
      }
      int hSpec = MeasureSpec.makeMeasureSpec(h, MeasureSpec.AT_MOST);
      for (int i = 0; i < getChildCount(); i++) {
         View view = getChildAt(i);
         view.measure(widthMeasureSpec, hSpec);
         h = Math.min(h, view.getMeasuredHeight());
      }
      setMeasuredDimension(w, getChildCount() == 0 ? 0 : h);
   }
}
