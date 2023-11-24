package com.example.socialmediaapp.customview.textview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ClickableTextView extends androidx.appcompat.widget.AppCompatTextView {
    private boolean isPressed;

    public ClickableTextView(@NonNull Context context) {
        super(context);
        init();
    }

    public ClickableTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ClickableTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isPressed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isPressed) {
                            isPressed = false;
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

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();
        Paint paint = new Paint();

        if (isPressed) {
            paint.setColor(Color.argb(10, 0, 0, 0));
            canvas.drawRect(0, 0, w, h, paint);
        }
        super.onDraw(canvas);
    }
}
