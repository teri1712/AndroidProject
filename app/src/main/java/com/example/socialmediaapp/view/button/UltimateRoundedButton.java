package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.Nullable;

public class UltimateRoundedButton extends CenteredContentButton {
   public UltimateRoundedButton(Context context) {
      super(context);
   }

   public UltimateRoundedButton(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public UltimateRoundedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   public UltimateRoundedButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int w = getWidth(), h = getHeight();
      int r = Math.min(w / 2, h / 2);
      Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
      bg.setColor(Color.WHITE);
      bg.setShadowLayer(8, 0, 0, Color.parseColor("#adb5bd"));
      canvas.drawRoundRect(8, 8, w - 8, h - 8, r, r, bg);
      super.onDraw(canvas);
   }
}
