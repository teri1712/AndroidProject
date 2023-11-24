package com.example.socialmediaapp.customview.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class DragPanel extends FrameLayout {

    private ScrollView childScroll;
    private int scrollId;
    private float prey, prex;
    private Runnable finishAction;

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.my_container);
        try {
            scrollId = a.getResourceId(R.styleable.my_container_scrollview_id, -1);
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (scrollId != -1) {
            childScroll = findViewById(scrollId);
        }
    }

    public void setFinishAction(Runnable finishAction) {
        this.finishAction = finishAction;
    }

    public DragPanel(@NonNull Context context) {
        super(context);
    }

    public DragPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DragPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DragPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (childScroll == null) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                setTranslationY(getTranslationY() + y - prey);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float curTrans = getTranslationY();
                if (curTrans > getHeight() / 4) {
                    animate().translationY(getHeight()).setDuration(200).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (finishAction != null) {
                                finishAction.run();
                            }
                        }
                    }).start();
                } else if (curTrans < -getHeight() / 4) {
                    animate().translationY(-getHeight()).setDuration(200).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            if (finishAction != null) {
                                finishAction.run();
                            }
                        }
                    }).start();
                }
                animate().translationY(0).setDuration(200).setInterpolator(new DecelerateInterpolator()).start();

                return false;
        }
        prey = event.getY();
        prex = event.getX();
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (childScroll == null) return false;
        float y = event.getY();
        float x = event.getX();
        boolean willIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (y > prey && childScroll.getScrollY() == 0) {
                    willIntercept = true;
                }
                if (y < prey && childScroll.getScrollY() == childScroll.getChildAt(0).getHeight()) {
                    willIntercept = true;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_DOWN:
                break;
            default:
                break;
        }
        prey = y;
        prex = x;

        return willIntercept;
    }
}
