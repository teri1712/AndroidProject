package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;

public class NotifyItemView extends FrameLayout {
    private View view;
    private boolean isRead;
    private boolean isPressed;

    void initView(Drawable avt, String notify_content, String cnt_time, boolean isRead) {
        this.isRead = isRead;
        CircleButton avatar_button = (CircleButton) view.findViewById(R.id.avatar);


        ((TextView) view.findViewById(R.id.notify_content)).setText(notify_content);
        ((TextView) view.findViewById(R.id.cnt_time)).setText(cnt_time);

        avatar_button.setBackgroundContent(avt, -1);
        //API on Button
    }

    public NotifyItemView(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.notify_item, this, true);
        isPressed = false;
        isRead = false;
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
    public void setIsRead() {
        this.isRead = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (!isRead) {
            Paint paint = new Paint();
            paint.setColor(Color.argb(0x10, 0x08, 0x66, 0xff));
            canvas.drawRect(0, 0, w, h, paint);
        }
        if (isPressed) {
            Paint paint = new Paint();
            paint.setColor(Color.argb(5, 0, 0, 0));
            canvas.drawRect(0, 0, w, h, paint);
        }
        super.onDraw(canvas);
    }
}
