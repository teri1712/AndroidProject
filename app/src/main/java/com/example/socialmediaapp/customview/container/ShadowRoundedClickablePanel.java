package com.example.socialmediaapp.customview.container;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.customview.container.ShadowRoundedPanel;

public class ShadowRoundedClickablePanel extends ShadowRoundedPanel {
    boolean isPressed;

    private void init() {
        isPressed = false;
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        animate().scaleX(0.95f).scaleY(0.95f).setDuration(50).start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isPressed) {
                            isPressed = false;
                            animate().scaleX(1).scaleY(1).setDuration(50).start();
                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                performClick();
                            }
                        }
                        break;
                    default:
                        break;
                }
                invalidate();
                return true;
            }
        });
    }

    public ShadowRoundedClickablePanel(@NonNull Context context) {
        super(context);
    }

    public ShadowRoundedClickablePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public ShadowRoundedClickablePanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }

    public ShadowRoundedClickablePanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();
        Paint paint = new Paint();

        if (isPressed) {
            paint.setColor(Color.argb(10, 0, 0, 0));
            canvas.drawRoundRect(15, 15, w - 15, h - 15, 15, 15, paint);
        }
        super.onDraw(canvas);
    }


}
