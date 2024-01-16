package com.example.socialmediaapp.view.mask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

public class AvatarCropMask extends View {
    public AvatarCropMask(Context context) {
        super(context);
    }

    public AvatarCropMask(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AvatarCropMask(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AvatarCropMask(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        p.setColor(Color.argb(200, 0, 0, 0));
        Path clipped = new Path();
        clipped.setFillType(Path.FillType.INVERSE_WINDING);
        clipped.addCircle(w / 2, h / 2, Math.min(w / 2, h / 2), Path.Direction.CCW);
        canvas.clipPath(clipped);
        canvas.drawRect(0, 0, w, h, p);
    }
}
