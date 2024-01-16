package com.example.socialmediaapp.layoutviews.items.message;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.Nullable;

public class TextingAnimateView extends View {
   private float hei;
   private ObjectAnimator animator;

   private void init() {
      animator = ObjectAnimator.ofFloat(this, "hei", -2f, 2f);
      animator.setDuration(1200);
      animator.setEvaluator(new FloatEvaluator());
      animator.setRepeatMode(ValueAnimator.RESTART);
      animator.setRepeatCount(ValueAnimator.INFINITE);
      animator.setInterpolator(null);
      hei = 0;
      setWillNotDraw(false);
   }

   public TextingAnimateView(Context context) {
      super(context);
      init();
   }

   public TextingAnimateView(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init();
   }

   public TextingAnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init();
   }

   public TextingAnimateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init();
   }

   public void performAnimation() {
      animator.start();
   }

   public void endAnimation() {
      animator.cancel();
   }

   @Override
   protected void onDetachedFromWindow() {
      animator.cancel();
      super.onDetachedFromWindow();
   }

   public float getHei() {
      return hei;
   }

   public void setHei(float hei) {
      this.hei = hei;
      invalidate();
   }

   private int toDp(float val) {
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getContext().getResources().getDisplayMetrics());
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int r = toDp(3.4f);
      int h = getHeight() - 2 * r;
      float x = hei < 0 ? -(hei + 1) : (hei - 1);
      float pos1 = h * x;
      float pos2 = calPost(pos1, hei >= 0);
      float pos3 = calPost(pos2, calOri(pos1, hei >= 0));

      int h1 = (int) Math.max(pos1, 0);
      int h2 = (int) Math.max(pos2, 0);
      int h3 = (int) Math.max(pos3, 0);

      int padd = toDp(4);

      int cx = r, cy = getHeight() - (h1 + r);
      Paint p = new Paint();
      p.setColor(Color.parseColor("#6fffffff"));
      canvas.drawCircle(cx, cy, r, p);
      cx += 2 * r + padd;
      cy = getHeight() - (h2 + r);
      canvas.drawCircle(cx, cy, r, p);
      cx += 2 * r + padd;
      cy = getHeight() - (h3 + r);
      canvas.drawCircle(cx, cy, r, p);
   }

   private float calPost(float pos, boolean up) {
      int h = getHeight() - toDp(6.8f);
      int delay = h - toDp(5);
      if (up) {
         return pos >= -(h - delay) ? pos - delay : -h + (delay - (pos + h));
      }
      return pos <= (h - delay) ? pos + delay : h - (delay - (h - pos));
   }

   private boolean calOri(float pos, boolean up) {
      int h = getHeight() - toDp(6.8f);
      int delay = h - toDp(5);
      if (up) {
         return pos >= -(h - delay) ? up : !up;
      }
      return pos <= (h - delay) ? up : !up;
   }
}
