package com.example.socialmediaapp.customview.progress.spinner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.graphics.Path;

import androidx.annotation.Nullable;

public class SpinningLoadPageView extends View {


    private int cur = 0;
    private boolean onAnimation = false;
    private int aniTurn = 0;
    private int head_angle, tail_angle;

    public SpinningLoadPageView(Context context) {
        super(context);
    }

    public SpinningLoadPageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinningLoadPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinningLoadPageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setProgress(int progress_angle) {
        cur = progress_angle;
        invalidate();
    }

    private int getSweepAngle(float progress) {
        return (int) (Math.pow(progress, 5) * 310);
    }

    private float cal_velo(int cur_angle) {
        if (cur_angle >= 180) {
            return Math.max(0.5f, 8 - ((float) cur_angle - 180) * 8 / 180);
        }

        return Math.max(0.1f, (float) cur_angle * 8 / 180);
    }

    public void perfromLoadingAnimation() {
        onAnimation = true;
        head_angle = 12;
        tail_angle = 360 - 12;
        new Thread(new Runnable() {
            int aniFrame = 60;
            int thisTurn = ++aniTurn;

            @Override
            public void run() {
                while (thisTurn == aniTurn) {
                    post(new Runnable() {
                        @Override
                        public void run() {
                            if (thisTurn != aniTurn) return;
                            int dh = (head_angle >= 360 - 12 || head_angle <= 12) ? 1 : 12;
                            int dt = (tail_angle >= 360 - 12 || tail_angle <= 12) ? 1 : 12;
                            head_angle = (head_angle + dh) % 360;
                            tail_angle = (tail_angle + dt) % 360;
                            invalidate();
                        }
                    });
                    try {
                        Thread.sleep(1000 / aniFrame);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
    }

    public void performEndLoadingAnimation() {
        animate().scaleX(0.2f).scaleY(0.2f).setDuration(100).withEndAction(new Runnable() {
            @Override
            public void run() {
                onAnimation = false;
                SpinningLoadPageView.this.setVisibility(View.GONE);
                ++aniTurn;
                SpinningLoadPageView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        SpinningLoadPageView.this.setTranslationY(0);
                        SpinningLoadPageView.this.setScaleX(1);
                        SpinningLoadPageView.this.setScaleY(1);
                    }
                });
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(Color.WHITE);
        bg.setShadowLayer(8, 0, 0, Color.parseColor("#ced4da"));
        int c = w / 2;
        canvas.drawCircle(c, c, c - 8, bg);

        float p = Math.min(1f, (float) cur / 220);

        Paint spin = new Paint();

        spin.setAntiAlias(true);
        //draw a stroke line
        spin.setStyle(Paint.Style.STROKE);
        spin.setStrokeWidth(6f);


        int r = (w - 68) / 2;
        RectF oval = new RectF(c - r, c - r, c + r, c + r);

        if (onAnimation) {
            spin.setColor(Color.argb(255, 0x08, 0x66, 0xFF));
            int sweep_angle = head_angle - tail_angle;
            if (sweep_angle < 0) sweep_angle += 360;
            canvas.drawArc(oval, tail_angle - 90, sweep_angle, false, spin);

            super.onDraw(canvas);
            return;
        }

        int alpha_value = (int) (Math.pow(p, 5) * 255);
        spin.setColor(Color.argb(alpha_value, 0x08, 0x66, 0xFF));
        int sweep_angle = getSweepAngle(p);
        canvas.drawArc(oval, cur - 90, sweep_angle, false, spin);


        Paint arrow = new Paint();
        arrow.setColor(Color.argb(alpha_value, 0x08, 0x66, 0xFF));
        arrow.setStyle(Paint.Style.FILL);
        arrow.setAntiAlias(true);

        int cur_target_angle = (sweep_angle + cur) % 360;
        int rotate_angle = 360 - cur_target_angle;
        Point offset_to_paint_array = new Point(c, c - r);

        Path triangle = new Path();

        int triangle_side = (int) (Math.ceil(p * 13));
        triangle.moveTo(offset_to_paint_array.x + triangle_side, offset_to_paint_array.y);
        triangle.lineTo(offset_to_paint_array.x, offset_to_paint_array.y + triangle_side);
        triangle.lineTo(offset_to_paint_array.x, offset_to_paint_array.y - triangle_side);
        triangle.close();
        canvas.rotate(-rotate_angle, c, c);
        canvas.drawPath(triangle, arrow);
        canvas.rotate(rotate_angle, c, c);


        super.onDraw(canvas);
    }
}
