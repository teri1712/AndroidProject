package com.example.socialmediaapp.customview.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.socialmediaapp.R;

public class ActiveFragmentButton extends View {
    private int active_drawable_resource;
    private int inactive_drawable_resource;
    protected boolean isActive;
    private boolean isPressed;

    protected void init(AttributeSet attrs) {
        setBackground(null);
        isPressed = false;
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.normal_button);
        try {
            active_drawable_resource = a.getResourceId(R.styleable.normal_button_active_background, -1);
            inactive_drawable_resource = a.getResourceId(R.styleable.normal_button_inactive_background, -1);
            isActive = a.getBoolean(R.styleable.normal_button_is_active, false);
        } finally {
            a.recycle();
        }

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
                            if (event.getAction() != MotionEvent.ACTION_CANCEL)
                                performClick();
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

    public void setActive(boolean isActive) {
        if (this.isActive == isActive) return;
        this.isActive = isActive;
        invalidate();
    }

    public ActiveFragmentButton(@NonNull Context context) {
        super(context);
    }

    public ActiveFragmentButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ActiveFragmentButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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


        int background_to_draw = inactive_drawable_resource;
        if (isActive && active_drawable_resource != -1)
            background_to_draw = active_drawable_resource;


        if (background_to_draw == active_drawable_resource) {
            Drawable bg = getResources().getDrawable(active_drawable_resource, null);
            int to_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getContext().getResources().getDisplayMetrics());
            int _left = (w - to_dp) / 2, _top = (h - to_dp) / 2;
            bg.setBounds(_left, _top, _left + to_dp, _top + to_dp);
            bg.draw(canvas);
            Paint c = new Paint();
            c.setARGB(20, 0x08, 0x66, 0xFF);
            int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, getContext().getResources().getDisplayMetrics());
            canvas.drawCircle(w / 2, h / 2, r, c);
        } else if (background_to_draw != -1) {
            Drawable bg = getResources().getDrawable(inactive_drawable_resource, null);
            int to_dp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getContext().getResources().getDisplayMetrics());
            int _left = (w - to_dp) / 2, _top = (h - to_dp) / 2;
            bg.setBounds(_left, _top, _left + to_dp, _top + to_dp);
            bg.draw(canvas);
        }
        super.onDraw(canvas);
    }

}
