package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class RoundedButton extends CenteredContentButton {


   private boolean isPressed;

   @Override
   protected void init(AttributeSet attrs) {
      super.init(attrs);
      isPressed = false;
      if (bg_color == 0) bg_color = Color.argb(15, 0, 0, 0);
      setOnTouchListener(new View.OnTouchListener() {
         @Override
         public boolean onTouch(View v, MotionEvent event) {
            if (isClickedEnable) {
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
                     if (event.getAction() == MotionEvent.ACTION_UP) requestFocus();
                     break;
                  default:
                     break;
               }
               invalidate();
               return (MotionEvent.ACTION_MOVE != event.getAction());
            }
            return false;
         }
      });
   }

   @Override
   public void setClickedEnable(boolean clickedEnable) {
      super.setClickedEnable(clickedEnable);
   }


   public RoundedButton(@NonNull Context context) {
      super(context);
   }

   public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public RoundedButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   @Override
   protected void drawBackgroundColor(Canvas canvas, int w, int h) {
      Paint bg = new Paint();
      bg.setColor((isClickedEnable ? bg_color : Color.argb(15, 0, 0, 0)));
      canvas.drawRoundRect(0, 0, w, h, 12, 12, bg);
   }

   public void setBackgroundColor(int color) {
      this.bg_color = color;
      invalidate();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int h = getHeight(), w = getWidth();
      Paint paint = new Paint();

      if (isPressed) {
         paint.setColor(Color.argb(15, 0, 0, 0));
         canvas.drawRoundRect(0, 0, w, h, 23, 23, paint);
      }


      super.onDraw(canvas);
   }
}
