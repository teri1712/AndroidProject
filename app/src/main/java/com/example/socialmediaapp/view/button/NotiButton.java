package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class NotiButton extends ActiveFragmentButton {
   private int count = 0;

   public NotiButton(@NonNull Context context) {
      super(context);
   }

   public NotiButton(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
   }

   public NotiButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
   }

   private int toDp(float val) {
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getContext().getResources().getDisplayMetrics());
   }

   public void setCount(int count) {
      this.count = count;
      invalidate();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      if (count == 0) return;
      int iconW = toDp(24);
      int rm = (getWidth() + iconW) / 2, tm = (getHeight() - iconW) / 2;
      Paint text = new Paint();
      text.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, getResources().getDisplayMetrics()));
      text.setColor(Color.WHITE);
      text.setTypeface(Typeface.create("Roboto", Typeface.NORMAL));
      float txtW = text.measureText(Integer.toString(count));
      float txtH = text.descent() - text.ascent();

      Paint red = new Paint();
      red.setColor(Color.parseColor("#e5383b"));
      int r = toDp(9.5f), pdTop = -toDp(6f), pdRight = -toDp(8);

      RectF oval = new RectF(rm - pdRight - 2 * r, tm + pdTop - 1.5f, rm - pdRight, tm + pdTop + 2 * r + 1.5f);
      canvas.drawOval(oval, red);

//      canvas.drawCircle(rm - pdRight - r, tm + pdTop + r, r, red);

      float x = rm - pdRight - 2 * r + (2 * r - txtW) / 2;
      float y = tm + pdTop + (2 * r - txtH) / 2 - text.ascent();

      canvas.drawText(Integer.toString(count), x, y, text);
   }
}
