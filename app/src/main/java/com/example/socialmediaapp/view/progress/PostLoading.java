package com.example.socialmediaapp.view.progress;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.view.mask.PostItemLoadingMask;

public class PostLoading extends LinearLayout {
   private ObjectAnimator skeletonAnimator;
   private float dummyBackgroundAlpha;

   public void setDummyBackgroundAlpha(float val) {
      dummyBackgroundAlpha = val;
      invalidate();
   }

   public float getDummyBackgroundAlpha() {
      return dummyBackgroundAlpha;
   }

   public PostLoading(@NonNull Context context) {
      super(context);
      setWillNotDraw(false);
      setOrientation(VERTICAL);
      setLayerType(LAYER_TYPE_HARDWARE, null);
      setBackgroundColor(Color.WHITE);
      for (int i = 0; i < 1; i++) {
         View loadingMask = new PostItemLoadingMask(getContext());
         LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, toDp(300));
         loadingMask.setLayoutParams(params);
         loadingMask.setLayerType(LAYER_TYPE_HARDWARE, null);
         addView(loadingMask);
      }
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
      params.topMargin = toDp(7);
      setLayoutParams(params);
      skeletonAnimator = ObjectAnimator.ofFloat(this, "dummyBackgroundAlpha", 0.1f, 0.3f);
      skeletonAnimator.setDuration(1000);
      skeletonAnimator.setEvaluator(new FloatEvaluator());
      skeletonAnimator.setRepeatMode(ValueAnimator.REVERSE);
      skeletonAnimator.setRepeatCount(ValueAnimator.INFINITE);
   }

   private int toDp(int val) {
      return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getContext().getResources().getDisplayMetrics());
   }


   public void start() {
      skeletonAnimator.start();
   }

   public void cancel() {
      skeletonAnimator.cancel();
   }

   @Override
   protected void onDetachedFromWindow() {
      cancel();
      super.onDetachedFromWindow();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      Paint c = new Paint();
      int w = getWidth(), h = getHeight();
      c.setColor(Color.argb((int) (dummyBackgroundAlpha * 255), 0x80, 0x80, 0x80));
      canvas.drawRect(0, 0, w, h, c);

   }
}
