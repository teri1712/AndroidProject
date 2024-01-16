package com.example.socialmediaapp.view.container;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TopHalfRoundedPanel extends FrameLayout {

    boolean isPressed;

    private void init() {
        setWillNotDraw(false);
        isPressed = false;
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        invalidate();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        if (isPressed) {
                            isPressed = false;
                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (isPressed) {
                            isPressed = false;
                            invalidate();
                            performClick();
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    public TopHalfRoundedPanel(@NonNull Context context) {
        super(context);
        init();
    }

    public TopHalfRoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TopHalfRoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TopHalfRoundedPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setStyle(Paint.Style.FILL);
        Path shape = new Path();
        shape.moveTo(0, 15);
        shape.quadTo(0, 0, 15, 0);
        shape.lineTo(w - 15, 0);
        shape.quadTo(w, 0, w, 15);
        shape.lineTo(w, h);
        shape.lineTo(0, h);
        shape.close();

        int color = isPressed ? Color.argb(10, 0, 0, 0) : Color.WHITE;
        bg.setColor(color);
        canvas.drawPath(shape, bg);
        super.onDraw(canvas);
    }
}
