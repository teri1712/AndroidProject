package com.example.socialmediaapp.view.message;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MessageClipFrame extends FrameLayout {

   private ClipMessageHelper clipHelper = new RoundedClipMessage(RoundedClipMessage.START | RoundedClipMessage.END, 1);

   public MessageClipFrame(@NonNull Context context) {
      super(context);
      setWillNotDraw(false);
   }

   public MessageClipFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      setWillNotDraw(false);
   }

   public MessageClipFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      setWillNotDraw(false);
   }

   public MessageClipFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      setWillNotDraw(false);
   }

   public void setClipHelper(ClipMessageHelper clipHelper) {
      this.clipHelper = clipHelper;
      invalidate();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      if (clipHelper == null) return;
      int w = getWidth(), h = getHeight();
      clipHelper.doClip(canvas, new Rect(0, 0, w, h), true);
      super.onDraw(canvas);
   }
}
