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

public class FriendRequestItemView extends FrameLayout {
    private View view;
    private boolean isPressed;

    void initView(Drawable avt, String name, String waiting_time, String mutual_friends) {
        CircleButton avatar_button = (CircleButton) view.findViewById(R.id.avatar);


        ((TextView) view.findViewById(R.id.name)).setText(name);
        ((TextView) view.findViewById(R.id.waiting_time)).setText(waiting_time);
        ((TextView) view.findViewById(R.id.mutual_friends)).setText(mutual_friends);

        avatar_button.setBackgroundContent(avt, -1);
        //API on Button
    }

    public FriendRequestItemView(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(R.layout.friend_request_item, this, true);
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

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        if (isPressed) {
            Paint paint = new Paint();
            paint.setColor(Color.argb(5, 0, 0, 0));
            canvas.drawRect(0, 0, w, h, paint);
        }
        super.onDraw(canvas);
    }
}
