package com.example.socialmediaapp.view.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class CircleConainer extends FrameLayout {
   private int color;

   private void init(AttributeSet attrs) {
      setWillNotDraw(false);
      TypedArray a = getContext().obtainStyledAttributes(
              attrs,
              R.styleable.normal_button);
      try {
         color = a.getColor(R.styleable.normal_button_button_background_color, 0);
      } finally {
         a.recycle();
      }
   }

   public CircleConainer(@NonNull Context context) {
      super(context);
   }

   public CircleConainer(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public CircleConainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int h = getHeight(), w = getWidth();

      Path clipped = new Path();
      clipped.addCircle(w / 2, w / 2, w / 2, Path.Direction.CCW);
      canvas.clipPath(clipped);

      Paint p = new Paint();
      p.setColor(color);
      canvas.drawRect(0, 0, w, h, p);
   }
}
