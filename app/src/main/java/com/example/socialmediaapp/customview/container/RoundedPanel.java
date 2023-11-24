package com.example.socialmediaapp.customview.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class RoundedPanel extends FrameLayout {

    private int roundedCorner;
    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(
                    attrs,
                    R.styleable.custom_edit_text);
            try {
                roundedCorner = a.getInt(R.styleable.custom_edit_text_custom_corner_ratio, 15);
            } finally {
                a.recycle();
            }
        }
    }


    public RoundedPanel(@NonNull Context context) {
        super(context);
    }

    public RoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);init(attrs);

    }

    public RoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);init(attrs);

    }

    public RoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);init(attrs);

    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.argb(15, 0, 0, 0));
        canvas.drawRoundRect(0,0, w , h , roundedCorner, roundedCorner, bg);
        super.onDraw(canvas);
    }


}
