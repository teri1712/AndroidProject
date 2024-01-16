package com.example.socialmediaapp.view.container;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

public class NoFocusChildScrollView extends ScrollView {
    public NoFocusChildScrollView(Context context) {
        super(context);
    }

    public NoFocusChildScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoFocusChildScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NoFocusChildScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        return false;
    }
}
