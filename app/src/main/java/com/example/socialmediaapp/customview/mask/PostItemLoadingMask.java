package com.example.socialmediaapp.customview.mask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class PostItemLoadingMask extends View {
    public PostItemLoadingMask(Context context) {
        super(context);
    }

    public PostItemLoadingMask(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PostItemLoadingMask(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public PostItemLoadingMask(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int toDp(int val) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, val, getContext().getResources().getDisplayMetrics());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setColor(Color.WHITE);
        Path clipped = new Path();
        clipped.setFillType(Path.FillType.INVERSE_WINDING);
        clipped.addCircle(toDp(30), toDp(30), toDp(20), Path.Direction.CCW);
        clipped.addRoundRect(new RectF(toDp(65), toDp(15), toDp(65 + 120), toDp(15 + 10)), 30, 30, Path.Direction.CCW);
        clipped.addRoundRect(new RectF(toDp(65), toDp(35), toDp(65 + 80), toDp(35 + 10)), 30, 30, Path.Direction.CCW);

        clipped.addRoundRect(new RectF(toDp(10), toDp(65), toDp(10 + 300), toDp(65 + 10)), 30, 30, Path.Direction.CCW);

        clipped.addRoundRect(new RectF(toDp(10), toDp(85), toDp(10 + 350), toDp(85 + 10)), 30, 30, Path.Direction.CCW);

        clipped.addRoundRect(new RectF(toDp(10), toDp(105), toDp(10 + 250), toDp(105 + 10)), 30, 30, Path.Direction.CCW);


        canvas.clipPath(clipped);
        canvas.drawRect(0, 0, w, h, p);
    }
}
