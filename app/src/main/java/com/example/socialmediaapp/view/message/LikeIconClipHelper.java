package com.example.socialmediaapp.view.message;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

import com.example.socialmediaapp.view.icon.IconUtils;

public class LikeIconClipHelper implements ClipMessageHelper {
   @Override
   public void doClip(Canvas canvas, Rect bound, boolean inner) {
      Path likePath = IconUtils.loadLikePath();
      likePath.offset(bound.left, bound.top);
      canvas.clipOutPath(likePath);
   }
}
