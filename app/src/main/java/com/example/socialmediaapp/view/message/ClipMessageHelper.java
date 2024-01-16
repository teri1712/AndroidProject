package com.example.socialmediaapp.view.message;

import android.graphics.Canvas;
import android.graphics.Rect;

public interface ClipMessageHelper {

   void doClip(Canvas canvas, Rect bound, boolean inner);
}
