package com.example.socialmediaapp.customview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.example.socialmediaapp.R;

public class DefaultBackgroundEditText extends AppCompatEditText {
    private String defaultText;

    private void init(AttributeSet attrs) {
        setBackground(null);
        defaultText = "";
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(DefaultBackgroundEditText.this, InputMethodManager.SHOW_IMPLICIT);
                    setSelection(getText().length());
                } else {
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindowToken(), 0);
                }
            }
        });
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.custom_edit_text);
        try {
            defaultText = a.getString(R.styleable.custom_edit_text_default_info);
        } finally {
            a.recycle();
        }
    }

    public DefaultBackgroundEditText(@NonNull Context context) {
        super(context);

    }

    public DefaultBackgroundEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);

    }

    public DefaultBackgroundEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    @Override
    protected void onDraw(Canvas canvas) {

        int h = getHeight(), w = getWidth();
        if (getText().length() == 0) {
            Paint txt = new Paint();
            txt.setTextSize(getTextSize());
            txt.setAntiAlias(true);
            txt.setColor(Color.GRAY);
            txt.setTypeface(Typeface.create("Roboto", Typeface.NORMAL));


            float offset_y = ((getGravity() & Gravity.TOP) != Gravity.TOP) ? ((h - txt.ascent() - txt.descent()) / 2) : (getPaddingTop() - txt.ascent());


            canvas.drawText(defaultText, getPaddingLeft(), offset_y, txt);
        }
        super.onDraw(canvas);

    }


}
