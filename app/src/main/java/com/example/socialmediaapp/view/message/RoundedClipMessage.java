package com.example.socialmediaapp.view.message;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;

public class RoundedClipMessage implements ClipMessageHelper {

   public static int START = 1, END = 2, MIDDLE = 4;
   private int pos;
   private int ori;
   public static int RIGHT = 0, LEFT = 1;

   public RoundedClipMessage(int pos, int ori) {
      this.pos = pos;
      this.ori = ori;
   }

   private Path clipRight(Rect bound, int R, int r) {
      Path clipped = new Path();
      clipped.moveTo(bound.left + R, bound.top);
      clipped.arcTo(bound.left, bound.top, bound.left + 2 * R, bound.top + 2 * R, -90f, -90f, false);
      clipped.lineTo(bound.left, bound.bottom - R);
      clipped.arcTo(bound.left, bound.bottom - 2 * R, bound.left + 2 * R, bound.bottom, -180f, -90f, false);
      int rr = (pos & END) == END ? R : r;
      clipped.lineTo(bound.right - rr, bound.bottom);
      clipped.arcTo(bound.right - 2 * rr, bound.bottom - 2 * rr, bound.right, bound.bottom, -270f, -90f, false);
      rr = (pos & START) == START ? R : r;
      clipped.lineTo(bound.right, bound.top + rr);
      clipped.arcTo(bound.right - 2 * rr, bound.top, bound.right, bound.top + 2 * rr, 0f, -90f, false);
      clipped.close();
      return clipped;
   }

   private Path clipLeft(Rect bound, int R, int r) {
      Path clipped = new Path();
      clipped.moveTo(bound.right - R, bound.top);
      clipped.arcTo(bound.right - 2 * R, bound.top, bound.right, bound.top + 2 * R, -90f, 90f, false);
      clipped.lineTo(bound.right, bound.bottom - R);
      clipped.arcTo(bound.right - 2 * R, bound.bottom - 2 * R, bound.right, bound.bottom, 0, 90f, false);
      int rr = (pos & END) == END ? R : r;
      clipped.lineTo(bound.left + rr, bound.bottom);
      clipped.arcTo(bound.left, bound.bottom - 2 * rr, bound.top + 2 * rr, bound.bottom, 90f, 90f, false);
      rr = (pos & START) == START ? R : r;
      clipped.lineTo(bound.left, bound.top + rr);
      clipped.arcTo(bound.left, bound.top, bound.left + 2 * rr, bound.top + 2 * rr, 180f, 90f, false);
      clipped.close();
      return clipped;
   }

   public void doClip(Canvas canvas, Rect bound, boolean inner) {
      int w = bound.right - bound.left;
      int h = bound.bottom - bound.top;
      int R = Math.min(60, Math.min(w / 2, h / 2)), r = 10;
      if (inner) {
         canvas.clipPath(ori == RIGHT ? clipLeft(bound, R, r) : clipRight(bound, R, r));
      } else {
         canvas.clipOutPath(ori == RIGHT ? clipLeft(bound, R, r) : clipRight(bound, R, r));
      }
   }
}
