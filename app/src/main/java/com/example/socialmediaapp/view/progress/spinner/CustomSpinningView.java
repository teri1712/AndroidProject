package com.example.socialmediaapp.view.progress.spinner;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class CustomSpinningView extends View {


   private boolean onAnimation = false;
   private int aniTurn = 0;
   private int head_angle, tail_angle;
   private int color;

   public CustomSpinningView(Context context) {
      super(context);
   }

   public CustomSpinningView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public CustomSpinningView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);

   }

   public CustomSpinningView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(attrs);

   }

   private void init(AttributeSet attrs) {
      TypedArray a = getContext().obtainStyledAttributes(
              attrs,
              R.styleable.normal_button);
      try {
         color = a.getColor(R.styleable.normal_button_button_background_color, Color.rgb(0x08, 0x66, 0xFF));
      } finally {
         a.recycle();
      }
   }

   @Override
   public void setVisibility(int visibility) {
      super.setVisibility(visibility);
      if (visibility == View.VISIBLE) {
         doLoading();
      } else {
         aniTurn++;
      }
   }

   @Override
   protected void onDetachedFromWindow() {
      aniTurn++;
      super.onDetachedFromWindow();
   }

   public void setColor(int color) {
      this.color = color;
   }


   private void doLoading() {
      onAnimation = true;
      head_angle = 12;
      tail_angle = 360 - 12;
      new Thread(new Runnable() {
         int aniFrame = 60;
         int thisTurn = ++aniTurn;

         @Override
         public void run() {
            while (thisTurn == aniTurn) {
               post(() -> {
                  if (thisTurn != aniTurn) return;
                  int dh = (head_angle >= 360 - 12 || head_angle <= 12) ? 1 : 12;
                  int dt = (tail_angle >= 360 - 12 || tail_angle <= 12) ? 1 : 12;
                  head_angle = (head_angle + dh) % 360;
                  tail_angle = (tail_angle + dt) % 360;
                  invalidate();
               });
               try {
                  Thread.sleep(1000 / aniFrame);
               } catch (InterruptedException e) {
               }
            }
         }
      }).start();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int w = getWidth(), h = getHeight();
      int c = w / 2;

      Paint spin = new Paint();

      spin.setAntiAlias(true);
      //draw a stroke line
      spin.setStyle(Paint.Style.STROKE);
      spin.setStrokeWidth(6f);


      int r = (w - 12) / 2;
      RectF oval = new RectF(c - r, c - r, c + r, c + r);

      if (onAnimation) {
         spin.setColor(color);
         int sweep_angle = head_angle - tail_angle;
         if (sweep_angle < 0) sweep_angle += 360;
         canvas.drawArc(oval, tail_angle - 90, sweep_angle, false, spin);

         super.onDraw(canvas);
         return;
      }


      super.onDraw(canvas);
   }
}
