package com.example.socialmediaapp.view.progress.state;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class PipeView extends View implements State {
    public PipeView(Context context) {
        super(context);
    }

    public PipeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PipeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PipeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private StateView nextState;

    private int progressColor;

    private float progress;

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public void setNextState(StateView nextState) {
        this.nextState = nextState;
    }

    @Override
    public void swithToCompleted() {
        progressColor = Color.parseColor("#009688");
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 1f);
        animator.setDuration(300).start();
        if (nextState != null) {
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    nextState.switchToInProgress();
                }
            });
        }
        invalidate();
    }

    @Override
    public void switchToInProgress() {
        progressColor = Color.parseColor("#0866FF");
        ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 0.5f);
        animator.setDuration(300).start();
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();

        Paint p = new Paint();
        p.setColor(Color.parseColor("#2F0866FF"));
        canvas.drawRect(0, 0, w, h, p);

        int pw = (int) (progress * w);

        Paint pp = new Paint();
        pp.setColor(progressColor);

        canvas.drawRect(0, 0, pw, h, pp);
    }


}
