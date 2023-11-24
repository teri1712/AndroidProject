package com.example.socialmediaapp.customview.button;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.socialmediaapp.R;

public class WavingButton extends AppCompatButton {
    private Point touchedPos;
    private int r;
    private int aniTurn;

    private int wave_color;

    private double distance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    protected void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.waving_button);
        try {
            wave_color = a.getColor(R.styleable.waving_button_wave_color, Color.argb(15, 0, 0, 0));
        } finally {
            a.recycle();
        }
        aniTurn = 0;
        setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchedPos = new Point((int) event.getX(), (int) event.getY());
                        Point a, b;
                        int w = getWidth(), h = getHeight();
                        if (touchedPos.x >= w - touchedPos.x) {
                            a = new Point(0, 0);
                            b = new Point(0, h);
                        } else {
                            a = new Point(w, 0);
                            b = new Point(w, h);
                        }
                        int max_r = (int) Math.ceil(Math.max(distance(touchedPos, a), distance(touchedPos, b)));
                        r = max_r * 90 / 100;

                        new Thread(new Runnable() {
                            int thisAniTurn = ++aniTurn;
                            int unit_r = (int) Math.ceil(((double) max_r - r) / 30);

                            @Override
                            public void run() {
                                for (int i = 0; i < 30; i++) {
                                    if (thisAniTurn != aniTurn) return;
                                    post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (thisAniTurn == aniTurn) {
                                                r += unit_r;
                                                invalidate();
                                            }
                                        }
                                    });
                                    try {
                                        Thread.sleep(2);
                                    } catch (InterruptedException e) {
                                    }
                                }
                            }
                        }).start();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        aniTurn++;
                        touchedPos = null;
                        if (MotionEvent.ACTION_UP == event.getAction())
                            performClick();
                        break;
                    default:
                        break;
                }
                invalidate();
                return true;
            }
        });
    }

    public WavingButton(@NonNull Context context) {
        super(context);
    }

    public WavingButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public WavingButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (touchedPos != null) {
            Paint wave = new Paint();
            wave.setColor(wave_color);
            canvas.drawCircle(touchedPos.x, touchedPos.y, r, wave);
        }
        super.onDraw(canvas);
    }

}
