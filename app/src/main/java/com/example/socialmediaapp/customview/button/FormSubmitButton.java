package com.example.socialmediaapp.customview.button;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FormSubmitButton extends WavingButton {

    public FormSubmitButton(@NonNull Context context) {
        super(context);
    }

    public FormSubmitButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FormSubmitButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        p.setColor(Color.argb(255, 0x08, 0x66, 0xFF));
        int h = getHeight(), w = getWidth();
        canvas.drawRoundRect(0, 0, w, h, 15, 15, p);
        super.onDraw(canvas);
    }
}
