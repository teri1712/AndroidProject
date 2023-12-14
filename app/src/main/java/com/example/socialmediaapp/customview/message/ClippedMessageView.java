package com.example.socialmediaapp.customview.message;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class ClippedMessageView extends FrameLayout {

    public static int START = 1, END = 2, MIDDLE = 4;

    private int position = START | END;

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.my_container);
        try {
            position = a.getInt(R.styleable.my_container_message_item_order, START);
        } finally {
            a.recycle();
        }
    }


    public ClippedMessageView(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public ClippedMessageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
        init(attrs);
    }

    public ClippedMessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        init(attrs);
    }

    public ClippedMessageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setWillNotDraw(false);
        init(attrs);
    }


    public void setPosition(int position) {
        this.position = position;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        int R = Math.min(55, Math.min(w / 2, h / 2)), r = 15;
        Path clipped = new Path();
        clipped.moveTo(R, 0);
        clipped.arcTo(0, 0, 2 * R, 2 * R, -90f, -90f, false);
        clipped.lineTo(0, h - R);
        clipped.arcTo(0, h - 2 * R, 2 * R, h, -180f, -90f, false);
        int rr = (position & END) == END ? R : r;
        clipped.lineTo(w - rr, h);
        clipped.arcTo(w - 2 * rr, h - 2 * rr, w, h, -270f, -90f, false);
        rr = (position & START) == START ? R : r;
        clipped.lineTo(w, rr);
        clipped.arcTo(w - 2 * rr, 0, w, 2 * rr, 0f, -90f, false);
        clipped.close();

        canvas.clipPath(clipped);
        super.onDraw(canvas);
    }
}
