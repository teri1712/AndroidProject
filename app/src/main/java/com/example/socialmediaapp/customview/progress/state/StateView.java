package com.example.socialmediaapp.customview.progress.state;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class StateView extends View implements State {
    //completed, not-reached, in progress
    private String state = "not-reached";
    private TextView textView;
    private PipeView pipe;


    public StateView(Context context) {
        super(context);
    }

    public StateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public StateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight(), w = getWidth();
        Paint p;
        switch (state) {
            case "not-reached":
                p = new Paint();
                p.setColor(Color.parseColor("#2F0866FF"));
                canvas.drawCircle(w / 2, h / 2, w / 2, p);
                break;
            case "in progress":
                p = new Paint();
                p.setColor(Color.parseColor("#0866FF"));
                canvas.drawCircle(w / 2, h / 2, w / 2, p);
                break;
            case "completed":
                p = new Paint();
                p.setColor(Color.parseColor("#009688"));
                canvas.drawCircle(w / 2, h / 2, w / 2, p);

                Drawable bg = getResources().getDrawable(R.drawable.check_24, null);
                int bound = 40 * w / 100;
                bg.setBounds((w - bound) / 2, (h - bound) / 2, (w + bound) / 2, (h + bound) / 2);
                bg.draw(canvas);
                break;
        }

        super.onDraw(canvas);
    }

    public void setPipe(PipeView pipe) {
        this.pipe = pipe;
    }

    public TextView getTextView() {
        return textView;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public void switchToInProgress() {
        state = "in progress";
        textView.setText(state);
        textView.setTextColor(Color.parseColor("#0866FF"));
        invalidate();
        if (pipe != null) {
            pipe.switchToInProgress();
        }
    }

    @Override
    public void swithToCompleted() {
        state = "completed";
        textView.setTextColor(Color.parseColor("#009688"));
        textView.setText(state);
        invalidate();
        if (pipe != null) {
            pipe.swithToCompleted();
        }
    }
}
