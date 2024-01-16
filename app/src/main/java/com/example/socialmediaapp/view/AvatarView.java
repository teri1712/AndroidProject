package com.example.socialmediaapp.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class AvatarView extends View {
   private Drawable bg;
   private float background_bound = 0;

   private void init(AttributeSet attrs) {
      TypedArray a = getContext().obtainStyledAttributes(
              attrs,
              R.styleable.normal_button);
      try {
         int background_resource = a.getResourceId(R.styleable.normal_button_button_background, -1);
         if (background_resource != -1) {
            bg = getResources().getDrawable(background_resource, null);
         }
         background_bound = a.getDimension(R.styleable.normal_button_background_bound, 0);
      } finally {
         a.recycle();
      }
   }

   public void setBackgroundContent(Drawable bg, int background_bound) {
      if (bg != null) {
         this.bg = bg;
      }
      if (background_bound != -1) {
         this.background_bound = background_bound;
      }
      invalidate();
   }

   public AvatarView(@NonNull Context context) {
      super(context);
   }

   public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int h = getHeight(), w = getWidth();

      Path clipped = new Path();
      clipped.addCircle(w / 2, w / 2, w / 2, Path.Direction.CCW);
      canvas.clipPath(clipped);
      if (bg != null) {
         int bg_wid = bg.getIntrinsicWidth(), bg_hei = bg.getIntrinsicHeight();
         float bound = (background_bound == 0) ? w : background_bound;
         float scale = (float) Math.max(bg_wid, bg_hei) / bound;
         bg_wid = (int) (bg_wid / scale);
         bg_hei = (int) (bg_hei / scale);
         int _left = (w - bg_wid) / 2, _top = (h - bg_hei) / 2;
         bg.setBounds(_left, _top, _left + bg_wid, _top + bg_hei);
         bg.draw(canvas);
      }
      super.onDraw(canvas);
   }
}
