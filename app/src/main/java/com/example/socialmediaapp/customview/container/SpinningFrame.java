package com.example.socialmediaapp.customview.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.progress.spinner.SpinningLoadPageView;

public class SpinningFrame extends FrameLayout {
    private ScrollView childScroll;
    private SpinningLoadPageView load_spinner;
    private float prey, prex;
    private int scrollId;
    private boolean loading;
    private Runnable action;

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.my_container);
        loading = false;
        try {
            scrollId = a.getResourceId(R.styleable.my_container_scrollview_id, -1);
        } finally {
            a.recycle();
        }
    }

    public void setAction(Runnable action) {
        this.action = action;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (scrollId != -1) {
            childScroll = findViewById(scrollId);
        }
        load_spinner = new SpinningLoadPageView(getContext());
        int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getContext().getResources().getDisplayMetrics());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(r, r);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        load_spinner.setLayoutParams(params);
        this.addView(load_spinner);
        load_spinner.setVisibility(View.GONE);
    }

    public SpinningFrame(@NonNull Context context) {
        super(context);
    }

    public SpinningFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public SpinningFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public SpinningFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (childScroll == null) return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float cur_trans = load_spinner.getTranslationY();
                float dif = (y - prey) / (1 + 3 * cur_trans / (400));
                float nxt_trans;
                if (y < prey) {
                    nxt_trans = Math.max(0.0f, load_spinner.getTranslationY() + dif);
                } else {
                    nxt_trans = Math.min(400, load_spinner.getTranslationY() + dif);
                }
                load_spinner.setTranslationY(nxt_trans);
                load_spinner.setProgress((int) (310 * nxt_trans / (400)));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float progress = 100 * load_spinner.getTranslationY() / 400;
                if (progress >= 70) {
                    loading = true;
                    load_spinner.animate().translationY(300).setDuration(100).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            load_spinner.perfromLoadingAnimation();
                            action.run();
                        }
                    }).start();
                } else {
                    load_spinner.setProgress(0);
                    load_spinner.animate().translationY(0).setDuration(150).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            load_spinner.setVisibility(View.GONE);
                        }
                    }).start();
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
        }
        prey = event.getY();
        prex = event.getX();
        return true;
    }

    public void endLoading() {
        if (!loading) return;
        loading = false;
        load_spinner.performEndLoadingAnimation();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        float y = event.getY();
        float x = event.getX();
        if (loading) {
            prey = y;
            prex = x;
            return false;
        }

        boolean willIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(prex - event.getX()) / Math.abs(prey - event.getY()) > 0.8f) {
                    break;
                }
                if (y > prey && childScroll.getScrollY() == 0 && load_spinner.getVisibility() == View.GONE) {
                    load_spinner.setVisibility(View.VISIBLE);
                    willIntercept = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
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
