package com.example.socialmediaapp.view.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.example.socialmediaapp.R;

public class CenteredContentButton extends View {


   protected Drawable bg;
   protected float background_bound;
   protected String text_content = "";
   protected float text_size;
   protected int textContentColor;
   protected float padding_between_text_and_background;
   protected int fontRes;
   protected int bg_color;
   protected boolean isClickedEnable;

   public boolean isClickedEnable() {
      return isClickedEnable;
   }

   public void setClickedEnable(boolean clickedEnable) {
      isClickedEnable = clickedEnable;
      invalidate();
   }

   public void setTextContentColor(int textContentColor) {
      this.textContentColor = textContentColor;
      invalidate();

   }

   public void setTextContent(String content) {
      this.text_content = content;
      invalidate();
   }

   public String getTextContent() {
      return text_content;
   }

   public void setBackgroundContent(Drawable bg) {
      this.bg = bg;
      invalidate();
   }

   public void setTextSize(float text_size) {
      this.text_size = text_size;
   }

   public void setPaddingBetweenTextAndBackground(float padding_between_text_and_background) {
      this.padding_between_text_and_background = padding_between_text_and_background;
   }

   public void setBackgroundBound(float background_bound) {
      this.background_bound = background_bound;
   }

   protected void init(AttributeSet attrs) {
      isClickedEnable = true;

      setFocusableInTouchMode(true);
      setFocusable(true);
      TypedArray a = getContext().obtainStyledAttributes(
              attrs,
              R.styleable.normal_button);
      try {
         int background_resource = a.getResourceId(R.styleable.normal_button_button_background, -1);
         if (background_resource != -1)
            bg = getResources().getDrawable(background_resource, null);

         text_content = a.getString(R.styleable.normal_button_button_text);
         if (text_content == null) text_content = "";
         text_size = a.getDimension(R.styleable.normal_button_button_text_size, 15f);
         textContentColor = a.getColor(R.styleable.normal_button_button_text_color, Color.BLACK);
         background_bound = a.getDimension(R.styleable.normal_button_background_bound, -1);
         padding_between_text_and_background = a.getDimension(R.styleable.normal_button_padding_between_text_and_background, 5);
         fontRes = a.getResourceId(R.styleable.normal_button_button_text_font, 0);
         bg_color = a.getColor(R.styleable.normal_button_button_background_color, 0);

      } finally {
         a.recycle();
      }
   }


   public CenteredContentButton(Context context) {
      super(context);
   }

   public CenteredContentButton(Context context, @Nullable AttributeSet attrs) {
      super(context, attrs);
      init(attrs);
   }

   public CenteredContentButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
      super(context, attrs, defStyleAttr);
      init(attrs);
   }

   public CenteredContentButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
      super(context, attrs, defStyleAttr, defStyleRes);
      init(attrs);
   }

   protected void drawBackgroundColor(Canvas canvas, int w, int h) {
      Paint bg = new Paint();
      bg.setColor((isClickedEnable ? bg_color : Color.argb(15, 0, 0, 0)));
      canvas.drawRect(0, 0, w, h, bg);
   }

   public void setBackgroundColor(int color) {
      this.bg_color = color;
      invalidate();
   }

   @Override
   protected void onDraw(Canvas canvas) {
      int h = getHeight(), w = getWidth();
      if (bg_color != 0) {
         drawBackgroundColor(canvas, w, h);
      }
      Paint text = new Paint();
      text.setTextSize(text_size);
      text.setColor((isClickedEnable ? textContentColor : Color.argb(150, 0, 0, 0)));
      if (fontRes == 0) {
         text.setTypeface(Typeface.create("Roboto", Typeface.NORMAL));
      } else {
         text.setTypeface(ResourcesCompat.getFont(getContext(), fontRes));
      }
      text.setAntiAlias(true);

      int width_text = (int) text.measureText(text_content);
      float offset_to_paint_text_x = (w - width_text) / 2;
      float offset_to_paint_text_y = (h - text.descent() - text.ascent()) / 2;
      if (background_bound == -1) background_bound = Math.max(w, h);

      if (bg != null) {
         int bg_wid = bg.getIntrinsicWidth(), bg_hei = bg.getIntrinsicHeight();
         float scale = (float) Math.max(bg_wid, bg_hei) / background_bound;
         bg_wid = (int) (bg_wid / scale);
         bg_hei = (int) (bg_hei / scale);


         float offset_to_draw_x = (w - width_text - padding_between_text_and_background - bg_wid) / 2;
         offset_to_paint_text_x = offset_to_draw_x + bg_wid + padding_between_text_and_background;
         float offset_to_draw_y = (h - bg_hei) / 2;


         bg.setBounds((int) offset_to_draw_x, (int) offset_to_draw_y, (int) (offset_to_draw_x + bg_wid), (int) (offset_to_draw_y + bg_hei));
         bg.draw(canvas);
      }
      canvas.drawText(text_content, offset_to_paint_text_x, offset_to_paint_text_y, text);
      super.onDraw(canvas);
   }


}
