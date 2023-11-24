package com.example.socialmediaapp.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CircleStrokeView extends View {
    public CircleStrokeView(@NonNull Context context) {
        super(context);
    }

    public CircleStrokeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CircleStrokeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircleStrokeView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth();
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.WHITE);
        int r = 16;
        bg.setStyle(Paint.Style.STROKE);
        bg.setStrokeWidth(r);
        int c = w / 2;
        canvas.drawCircle(c, c, c - r / 2, bg);
        super.onDraw(canvas);
    }

}
