package com.example.socialmediaapp.customview.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.customview.button.CenteredContentButton;

public class PostButton extends CenteredContentButton {
    private boolean isPressed;

    @Override
    protected void init(AttributeSet attrs) {
        super.init(attrs);
        isPressed = false;
        setOnTouchListener(new View.OnTouchListener() {
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
                            if (MotionEvent.ACTION_UP == event.getAction()) performClick();
                        }

                        if (event.getAction() == MotionEvent.ACTION_UP) requestFocus();
                        break;
                    default:
                        break;
                }
                invalidate();
                return (MotionEvent.ACTION_MOVE != event.getAction());
            }
        });
    }

    public PostButton(@NonNull Context context) {
        super(context);
    }

    public PostButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PostButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();


        Paint paint = new Paint();
        if (!isPressed) {
            paint.setColor(Color.argb(0, 0, 0, 0));
        } else {
            paint.setColor(Color.argb(15, 0, 0, 0));
        }
        canvas.drawRect(0, 0, w, h, paint);
        super.onDraw(canvas);
    }

}
