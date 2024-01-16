package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class CircleButton extends View {
   private Drawable bg;
   private float background_bound;
   private boolean isPressed;
   private int bg_color;
   private boolean isClickedEnable;
   private int pressColor;

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
         bg_color = a.getColor(R.styleable.normal_button_button_background_color, -1);
         pressColor = a.getColor(R.styleable.normal_button_button_press_color, Color.argb(20, 0, 0, 0));
      } finally {
         a.recycle();
      }
      isClickedEnable = true;
      isPressed = false;
      setFocusableInTouchMode(true);
      setFocusable(true);
      setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            if (!isClickedEnable) return false;
            switch (event.getAction()) {
               case MotionEvent.ACTION_DOWN:
                  isPressed = true;
                  animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).start();
                  break;
               case MotionEvent.ACTION_UP:
               case MotionEvent.ACTION_CANCEL:
                  if (isPressed) {
                     isPressed = false;
                     animate().scaleX(1).scaleY(1).setDuration(50).start();
                     if (event.getAction() == MotionEvent.ACTION_UP) {
                        performClick();
                     }
                  }
                  if (event.getAction() == MotionEvent.ACTION_UP) {
                     requestFocus();
                  }
                  break;
               default:
                  break;
            }
            invalidate();
            return (MotionEvent.ACTION_MOVE != event.getAction());
         }
      });
   }


   public void setClickedEnable(boolean clickedEnable) {
      isClickedEnable = clickedEnable;
      invalidate();
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

   public CircleButton(@NonNull Context context) {
      super(context);
   }

   public CircleButton(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public CircleButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int h = getHeight(), w = getWidth();


      Path clipped = new Path();
      clipped.addCircle(w / 2, w / 2, w / 2, Path.Direction.CW);
      canvas.clipPath(clipped);
      if (bg_color != -1) {
         Paint p = new Paint();
         p.setColor(bg_color);
         canvas.drawRect(0, 0, w, h, p);
      }

      if (isPressed) {
         Paint p = new Paint();
         p.setColor(pressColor);
         canvas.drawRect(0, 0, w, h, p);
      }
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
