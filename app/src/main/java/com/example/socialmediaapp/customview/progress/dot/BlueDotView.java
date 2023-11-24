package com.example.socialmediaapp.customview.progress.dot;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class BlueDotView extends View {

    private boolean isActive;
    private float curScale = 0.8f;

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.normal_button);
        try {
            isActive = a.getBoolean(R.styleable.normal_button_is_active, false);
        } finally {
            a.recycle();
        }
    }

    public BlueDotView(Context context) {
        super(context);
    }

    public BlueDotView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BlueDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public BlueDotView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);

    }


    public void changeScale(float scale) {
        curScale = scale;
        animate().scaleX(curScale).scaleY(curScale).setDuration(100).start();
    }

    public void setActive(boolean active) {
        isActive = active;
        if (isActive) {
            animate().scaleY(1).scaleX(1).setDuration(100).start();
        } else {
            animate().scaleY(curScale).scaleX(curScale).setDuration(100).start();
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        Paint p = new Paint();
        p.setAntiAlias(true);
        int color = (isActive) ? Color.parseColor("#AF0866FF") : Color.parseColor("#2F000000");
        p.setColor(color);
        canvas.drawCircle(w / 2, h / 2, Math.min(w, h) / 2, p);
    }

}
