package com.example.socialmediaapp.view.message;

import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class RightMessageFrame extends FrameLayout {
   private ClipMessageHelper clipHelper;
   private float preX, preY;
   private float curTransX;
   private View msgView;
   private View draggedView;

   private void init(AttributeSet attrs) {
      setWillNotDraw(false);
      curTransX = 0;
   }

   @Override
   protected void onAttachedToWindow() {
      super.onAttachedToWindow();
   }

   public RightMessageFrame(@NonNull Context context) {
      super(context);
   }

   public RightMessageFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public RightMessageFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   public RightMessageFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(attrs);
   }

   public void setClipHelper(ClipMessageHelper clipHelper) {
      this.clipHelper = clipHelper;
      invalidate();
   }

   public void setCurTransX(float curTransX) {
      this.curTransX = curTransX;
      draggedView.setTranslationX(curTransX);
      invalidate();
   }

   @Override
   public boolean onTouchEvent(MotionEvent event) {
      if (event.getAction() == MotionEvent.ACTION_DOWN) return false;

      float x = event.getX();
      float y = event.getY();
      switch (event.getAction()) {
         case MotionEvent.ACTION_MOVE:
            float dif = x - preX;
            float transX = dif + draggedView.getTranslationX();
            transX = Math.min(0, transX);
            setCurTransX(transX);
            break;
         case MotionEvent.ACTION_CANCEL:
         case MotionEvent.ACTION_UP:
            ((ViewGroup) getParent()).requestDisallowInterceptTouchEvent(false);
            ObjectAnimator animator = new ObjectAnimator();

            animator = ObjectAnimator.ofFloat(this, "curTransX", draggedView.getTranslationX(), 0f);
            animator.setDuration(200);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setEvaluator(new FloatEvaluator());
            animator.start();

            return false;
      }
      preY = y;
      preX = x;
      return true;
   }

   @Override
   public boolean onInterceptTouchEvent(MotionEvent event) {
      float x = event.getX();
      float y = event.getY();
      boolean willIntercept = false;
      switch (event.getAction()) {
         case MotionEvent.ACTION_MOVE:
            if (Math.abs(x - preX) > 5 * Math.abs(y - preY)) {
               willIntercept = true;
            }
            break;
         default:
            break;
      }
      preX = x;
      preY = y;
      if (willIntercept) {
         ((ViewGroup) getParent()).requestDisallowInterceptTouchEvent(true);
      }
      return willIntercept;
   }

   @Override
   protected void onDraw(Canvas canvas) {
      super.onDraw(canvas);
      if (clipHelper != null) {
         Rect clipRect = new Rect();

         View cur = msgView;
         while (cur.getParent() != getParent()) {
            ViewGroup p = (ViewGroup) cur.getParent();
            p.offsetDescendantRectToMyCoords(cur, clipRect);
            cur = p;
         }
         clipRect.left += curTransX;
         clipRect.right = clipRect.left + msgView.getWidth();
         clipRect.bottom = clipRect.top + msgView.getHeight();

         clipHelper.doClip(canvas, clipRect, false);
      }

      Paint paint = new Paint();
      paint.setColor(Color.BLACK);

      canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

   }
}
