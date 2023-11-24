package com.example.socialmediaapp.customview.container;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ShadowRoundedPanel extends FrameLayout {
    public ShadowRoundedPanel(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public ShadowRoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public ShadowRoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public ShadowRoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.WHITE);
        bg.setShadowLayer(15, 0, 0, Color.parseColor("#ced4da"));
        canvas.drawRoundRect(15, 15, w - 15, h - 15, 15, 15, bg);
        super.onDraw(canvas);
    }


}
