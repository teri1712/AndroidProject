package com.example.socialmediaapp.customview.container;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class ClickablePanel extends FrameLayout {
    boolean isPressed;
    boolean requestPressPaint = true;

    private void init(AttributeSet attrs) {
        isPressed = false;
        setWillNotDraw(false);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(
                    attrs,
                    R.styleable.normal_button);
            try {
                requestPressPaint = a.getBoolean(R.styleable.normal_button_request_press_paint, true);
            } finally {
                a.recycle();
            }
        }

        setFocusableInTouchMode(true);
        setFocusable(true);
        setRequestPressPaint(false);
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

    public ClickablePanel(@NonNull Context context) {
        super(context);
        init(null);
    }

    public ClickablePanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ClickablePanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public ClickablePanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);

    }

    public void setRequestPressPaint(boolean requestPressPaint) {
        this.requestPressPaint = requestPressPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();
        Paint paint = new Paint();

        if (requestPressPaint && isPressed) {
            paint.setColor(Color.argb(10, 0, 0, 0));
            canvas.drawRect(0, 0, w, h, paint);
        }
        super.onDraw(canvas);
    }


}
