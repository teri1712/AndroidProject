package com.example.socialmediaapp.customview.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.socialmediaapp.R;

public class PostContentTextVIew extends androidx.appcompat.widget.AppCompatTextView {

    private boolean enable_extendable_feature;
    private boolean isExtended;
    private Point offset_to_paint_read_more;
    private int default_height;
    private boolean is_pressed;
    private Paint text;
    private String erased_tail;

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.post_text_view);
        try {
            String txt = a.getString(R.styleable.post_text_view_text_content);
            setStatusContent(txt);
        } finally {
            a.recycle();
        }
    }

    public PostContentTextVIew(@NonNull Context context) {
        super(context);
    }

    public PostContentTextVIew(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PostContentTextVIew(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void initEffect() {

        if (getLineCount() > 2) {
            enable_extendable_feature = true;
            isExtended = false;
            String txt = getText().toString();
            text = new Paint();
            text.setTextSize(getTextSize());
            text.setColor(Color.GRAY);
            text.setTypeface(Typeface.create("Roboto", Typeface.NORMAL));

            default_height = getHeight();
            Layout layout = getLayout();
            int s = layout.getLineEnd(0), e = layout.getLineEnd(1);
            if (e - s > 13) {
                s = e - 13;
            }
            erased_tail = txt.substring(s, Math.min(s + 13, e));
            offset_to_paint_read_more = new Point((int) layout.getPrimaryHorizontal(s), layout.getLineTop(1));

            setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        if (!isExtended) {
                            int x = (int) motionEvent.getX();
                            int y = (int) motionEvent.getY();
                            float hei = text.descent() - text.ascent();

                            if (x >= offset_to_paint_read_more.x + text.measureText("... ") && x <= offset_to_paint_read_more.x + text.measureText("... read more")) {
                                if (y >= offset_to_paint_read_more.y && y <= offset_to_paint_read_more.y + hei) {
                                    is_pressed = true;
                                    invalidate();
                                }
                            }
                        }
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (!isExtended) {
                            if (is_pressed) {
                                is_pressed = false;

                                int x = (int) motionEvent.getX();
                                int y = (int) motionEvent.getY();
                                float hei = text.descent() - text.ascent();
                                if (x >= offset_to_paint_read_more.x + text.measureText("... ") && x <= offset_to_paint_read_more.x + text.measureText("... read more")
                                        && y >= offset_to_paint_read_more.y && y <= offset_to_paint_read_more.y + hei) {
                                    isExtended = true;
                                    ViewGroup.LayoutParams params = getLayoutParams();
                                    params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    requestLayout();
                                } else {
                                    invalidate();
                                }
                            }
                        } else {
                            isExtended = false;
                            ViewGroup.LayoutParams params = getLayoutParams();
                            params.height = default_height;
                            requestLayout();
                        }
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                        int x = (int) motionEvent.getX();
                        int y = (int) motionEvent.getY();
                        float hei = text.descent() - text.ascent();
                        if (x < offset_to_paint_read_more.x + text.measureText("... ") || x > offset_to_paint_read_more.x + text.measureText("... read more")
                                || y < offset_to_paint_read_more.y || y > offset_to_paint_read_more.y + hei) {
                            if (is_pressed) {
                                is_pressed = false;
                                invalidate();
                            }
                        }
                        return true;
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                        if (!isExtended) {
                            if (is_pressed) {
                                is_pressed = false;
                                invalidate();
                            }
                        }
                        return true;
                    }
                    return false;
                }
            });
        } else if (getLineCount() == 1) {
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getContext().getResources().getDisplayMetrics());
            requestLayout();
        }
    }

    public void setStatusContent(String txt) {
        setText(txt);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                initEffect();
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (enable_extendable_feature && !isExtended) {
            float hei = text.descent() - text.ascent();
            Paint theme = new Paint();
            theme.setColor(Color.WHITE);
            int padd = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getContext().getResources().getDisplayMetrics());

            canvas.drawRect(offset_to_paint_read_more.x, offset_to_paint_read_more.y,
                    offset_to_paint_read_more.x + text.measureText(erased_tail) + 2 * padd, offset_to_paint_read_more.y + hei,
                    theme);
            if (is_pressed) {
                theme.setColor(Color.parseColor("#e9ecef"));
                canvas.drawRect(offset_to_paint_read_more.x + text.measureText("... "), offset_to_paint_read_more.y,
                        offset_to_paint_read_more.x + text.measureText("... Read more") + 2 * padd, offset_to_paint_read_more.y + hei,
                        theme);
            }
            text.setColor(Color.BLACK);
            canvas.drawText("...", offset_to_paint_read_more.x + padd, offset_to_paint_read_more.y - text.ascent(), text);
            text.setColor(Color.GRAY);
            canvas.drawText("Read more", offset_to_paint_read_more.x + padd + text.measureText("... "), offset_to_paint_read_more.y - text.ascent(), text);
        }
    }
}
