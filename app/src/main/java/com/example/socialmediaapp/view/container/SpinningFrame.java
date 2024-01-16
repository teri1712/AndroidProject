package com.example.socialmediaapp.view.container;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.view.progress.spinner.SpinningLoadPageView;

public class SpinningFrame extends FrameLayout {

    private SpinningLoadPageView load_spinner;
    private float prey, prex;
    private boolean loading;
    private SpinHelper helper;

    public interface SpinHelper {
        LiveData<?> doAction();

        boolean isAtTop();
    }

    public void setHelper(SpinHelper helper) {
        this.helper = helper;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        load_spinner = new SpinningLoadPageView(getContext());
        int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 44, getContext().getResources().getDisplayMetrics());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(r, r);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        load_spinner.setLayoutParams(params);
        addView(load_spinner);
        load_spinner.setVisibility(View.GONE);
    }

    public SpinningFrame(@NonNull Context context) {
        super(context);
    }

    public SpinningFrame(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinningFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinningFrame(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (helper == null) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float y = event.getY();
                float cur_trans = load_spinner.getTranslationY();
                float dif = (y - prey) / (1 + 3 * cur_trans / (400));
                float nxt_trans = (y < prey) ? Math.max(0.0f, cur_trans + dif) : Math.min(400, cur_trans + dif);
                load_spinner.setTranslationY(nxt_trans);
                if (nxt_trans <= 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                    return false;
                }
                load_spinner.setProgress((int) (310 * nxt_trans / (400)));
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float progress = 100 * load_spinner.getTranslationY() / 400;
                if (progress >= 70) {
                    doLoad();
                } else {
                    load_spinner.setProgress(0);
                    load_spinner.animate()
                            .translationY(0)
                            .setDuration(150)
                            .start();
                }
                getParent().requestDisallowInterceptTouchEvent(false);
                return false;
        }
        prey = event.getY();
        prex = event.getX();
        return true;
    }

    public void doLoad() {
        loading = true;
        load_spinner.animate()
                .translationY(300)
                .setDuration(100).start();
        load_spinner.performLoading();
        helper.doAction().observe((LifecycleOwner) getContext(), (Observer<Object>) o -> endSpin());
    }

    public void endSpin() {
        load_spinner.performEnd(() -> loading = false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (helper == null) return false;
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
                if (Math.abs(prex - event.getX()) / Math.abs(prey - event.getY()) > 0.8f)
                    break;
                if (y > prey && helper.isAtTop()) {
                    willIntercept = true;
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            default:
                break;
        }
        prey = y;
        prex = x;
        if (willIntercept) {
            load_spinner.setVisibility(VISIBLE);
            load_spinner.setTranslationY(0);
            load_spinner.setScaleX(1);
            load_spinner.setScaleY(1);
        }
        return willIntercept;
    }

}
