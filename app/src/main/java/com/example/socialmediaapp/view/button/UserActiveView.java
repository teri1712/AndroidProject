package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class UserActiveView extends View {
   public static UserState ACTIVE = new UserActive();
   public static UserState INACTIVE = new UserInActive();

   private interface UserState {
      void clip(Canvas canvas, int w, int h, int d, int pd);

      void paintState(Canvas canvas, int w, int h, int d, int pd);
   }


   private static class UserActive implements UserState {
      @Override
      public void clip(Canvas canvas, int w, int h, int d, int pd) {
         Path clipped = new Path();
         clipped.addCircle(w / 2, w / 2, w / 2, Path.Direction.CW);
         clipped.addCircle(w - d / 2 + pd / 2, h - d / 2, d / 2, Path.Direction.CW);
         canvas.clipPath(clipped);
      }

      @Override
      public void paintState(Canvas canvas, int w, int h, int d, int pd) {
         Paint p = new Paint();
         p.setColor(Color.BLACK);
         canvas.drawCircle(w - d / 2 + pd / 2, h - d / 2, d / 2, p);
         p.setColor(Color.rgb(49, 162, 76));
         canvas.drawCircle(w - d / 2 + pd / 2, h - d / 2, (d - pd) / 2, p);
      }
   }

   private static class UserInActive implements UserState {

      @Override
      public void clip(Canvas canvas, int w, int h, int d, int pd) {
         Path clipped = new Path();
         clipped.addCircle(w / 2, w / 2, w / 2, Path.Direction.CW);
         canvas.clipPath(clipped);
      }

      @Override
      public void paintState(Canvas canvas, int w, int h, int d, int pd) {
      }
   }

   public static class UserTimeActive implements UserState {
      private Paint text;
      private String time;
      private float txtW;
      private final int tPd = 18;
      private float txtH;

      public UserTimeActive(Integer min, Context context) {
         text = new Paint();
         text.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 8, context.getResources().getDisplayMetrics()));
         text.setColor(Color.BLACK);
         text.setTypeface(Typeface.create("Roboto", Typeface.NORMAL));
         time = min + "p";
         txtW = text.measureText(time);
         txtH = text.descent() - text.ascent();
      }

      @Override
      public void clip(Canvas canvas, int w, int h, int d, int pd) {
         Path clipped = new Path();
         clipped.addCircle(w / 2, w / 2, w / 2, Path.Direction.CW);
         clipped.addRoundRect(w - (txtW + tPd + pd) + pd / 2, h - d, w, h, 60, 60, Path.Direction.CW);
         canvas.clipPath(clipped);
      }

      @Override
      public void paintState(Canvas canvas, int w, int h, int d, int pd) {
         Paint p = new Paint();

         p.setColor(Color.BLACK);
         canvas.drawRoundRect(w - (txtW + tPd + pd) + pd / 2, h - d, w + pd / 2, h, 60, 60, p);

         p.setColor(Color.parseColor("#e9f5db"));
         canvas.drawRoundRect(w - (txtW + tPd + pd / 2) + pd / 2, h - d + pd / 2, w - pd / 2 + pd / 2, h - pd / 2, 60, 60, p);

         canvas.drawText(time, w - txtW - tPd / 2 - pd / 2 + pd / 2, h - d + pd / 2 + (d - pd - txtH) / 2 - text.ascent(), text);

      }
   }

   private Drawable bg;
   private float background_bound;
   private UserState userState = INACTIVE;

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

   public void setUserState(UserState userState) {
      this.userState = userState;
      invalidate();
   }

   public UserActiveView(@NonNull Context context) {
      super(context);
   }

   public UserActiveView(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public UserActiveView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int h = getHeight(), w = getWidth();
      int d = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 17, getContext().getResources().getDisplayMetrics());
      int pd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getContext().getResources().getDisplayMetrics());

      userState.clip(canvas, w, h, d, pd);
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
      userState.paintState(canvas, w, h, d, pd);

      super.onDraw(canvas);
   }
}
