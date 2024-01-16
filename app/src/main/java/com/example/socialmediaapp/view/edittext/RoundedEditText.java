package com.example.socialmediaapp.view.edittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class RoundedEditText extends DefaultBackgroundEditText {
    private int rc;
    private int bgColor;

    private void init(AttributeSet attrs) {
        setTextColor(Color.BLACK);
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.custom_edit_text);
        try {
            rc = a.getInt(R.styleable.custom_edit_text_custom_corner_ratio, 23);
            bgColor = a.getColor(R.styleable.custom_edit_text_background_color, Color.argb(15, 0, 0, 0));
        } finally {
            a.recycle();
        }
    }

    public RoundedEditText(@NonNull Context context) {
        super(context);

    }

    public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public RoundedEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Paint p = new Paint();
        p.setColor(bgColor);
        int h = getHeight(), w = getWidth();
        canvas.drawRoundRect(0, 0, w, h, rc, rc, p);
        super.onDraw(canvas);
    }


}
