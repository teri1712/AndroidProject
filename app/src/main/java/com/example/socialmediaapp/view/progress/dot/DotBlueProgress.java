package com.example.socialmediaapp.view.progress.dot;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

import java.util.ArrayList;
import java.util.List;

public class DotBlueProgress extends FrameLayout {

    private HorizontalScrollView root;
    private List<BlueDotView> progress;
    private ViewGroup container;
    private int n;
    private int cur, left, right;
    private int window_count;
    private int dot_size;
    private int dot_margin;

    public DotBlueProgress(@NonNull Context context) {
        super(context);
    }

    public DotBlueProgress(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DotBlueProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);

    }

    public DotBlueProgress(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);

    }

    public void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.progress_dot);
        try {
            n = a.getInt(R.styleable.progress_dot_count, 0);
            window_count = a.getInt(R.styleable.progress_dot_window_count, 3);
            dot_size = a.getInt(R.styleable.progress_dot_dot_size, 12);
            dot_margin = a.getInt(R.styleable.progress_dot_dot_margin, 2);
        } finally {
            a.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(getContext());
        root = (HorizontalScrollView) inflater.inflate(R.layout.blue_dot_progress, this, false);
        addView(root);
        progress = new ArrayList<>();
        container = (ViewGroup) root.getChildAt(0);
        for (int i = 0; i < n; i++) {
            BlueDotView dot = new BlueDotView(getContext());
            LinearLayout.LayoutParams dotPrams = new LinearLayout.LayoutParams(dot_size, dot_size);
            dotPrams.rightMargin = dot_margin;
            dotPrams.leftMargin = dot_margin;
            dot.setLayoutParams(dotPrams);
            progress.add(dot);
            container.addView(dot);
        }
        cur = 0;
        left = 0;
        right = Math.min(n, window_count) - 1;
        ViewGroup.LayoutParams rootParams = root.getLayoutParams();
        rootParams.width = (dot_size + 2 * dot_margin) * window_count;
        for (int i = left; i <= right; i++) {
            progress.get(i).changeScale(getScale(i));
        }
        if (n != 0) {
            progress.get(cur).setActive(true);
        }
    }

    private float getScale(int pos) {
        int c = (left + right) / 2;
        int r = window_count / 2;
        if (Math.abs(c - pos) == r) {
            return 0.5f;
        }
        return 0.8f;
    }

    public void nextPage() {
        if (cur == n - 1) return;
        if (++cur == right && cur != n - 1 && n > window_count) {
            int offset = (dot_margin * 2 + dot_size) * (left + 1);
            ObjectAnimator.ofInt(root, "scrollX", offset).setDuration(200).start();
            left++;
            right++;
            for (int i = Math.max(0, left - 1); i <= Math.min(right + 1, n - 1); i++) {
                progress.get(i).changeScale(getScale(i));
            }
        }
        progress.get(cur - 1).setActive(false);
        progress.get(cur).setActive(true);
    }

    public void prePage() {
        if (cur == 0) return;
        if (--cur == left && cur != 0 && n > window_count) {
            int offset = (dot_margin * 2 + dot_size) * (left - 1);
            ObjectAnimator.ofInt(root, "scrollX", offset).setDuration(200).start();
            left--;
            right--;
            for (int i = Math.max(left, 0); i <= Math.min(n - 1, right + 1); i++) {
                progress.get(i).changeScale(getScale(i));
            }
        }
        progress.get(cur + 1).setActive(false);
        progress.get(cur).setActive(true);
    }

}
