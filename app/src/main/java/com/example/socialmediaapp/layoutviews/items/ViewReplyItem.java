package com.example.socialmediaapp.layoutviews.items;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.home.fragment.SimpleLifecycleOwner;
import com.example.socialmediaapp.view.container.ClickablePanel;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.viewmodel.fragment.ReplyListViewModel;

public class ViewReplyItem extends LinearLayout {
   private SimpleLifecycleOwner lifecycleOwner;
   private ClickablePanel viewMoreReply;
   private TextView cntReplyTextView;
   private CustomSpinningView spinnerLoading;

   public ViewReplyItem(@NonNull Context context) {
      super(context);
      setOrientation(LinearLayout.VERTICAL);
      setWillNotDraw(false);
      LayoutInflater inflater = LayoutInflater.from(context);
      this.viewMoreReply = (ClickablePanel) inflater.inflate(R.layout.item_view_reply, this, false);
      this.cntReplyTextView = viewMoreReply.findViewById(R.id.count_reply_textview);
      this.spinnerLoading = new CustomSpinningView(context);
      int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, context.getResources().getDisplayMetrics());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(r, r);
      params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
      spinnerLoading.setLayoutParams(params);
      spinnerLoading.setColor(Color.parseColor("#757575"));
      spinnerLoading.setVisibility(View.GONE);
      addView(spinnerLoading);
      addView(viewMoreReply);
   }

   private void ensureNew() {
      if (lifecycleOwner != null) {
         lifecycleOwner.destroy();
      }
      lifecycleOwner = new SimpleLifecycleOwner();
   }

   public void initViewModel(
           LiveData<Boolean> loadReplyState,
           LiveData<Integer> countUnReadReply) {
      ensureNew();
      countUnReadReply.observe(lifecycleOwner, new Observer<Integer>() {
         @Override
         public void onChanged(Integer integer) {
            integer  = Math.max(0, integer);
            if (integer == 0) {
               setVisibility(View.GONE);
            } else {
               setVisibility(View.VISIBLE);
               cntReplyTextView.setText("View replies (" + integer + ")");
            }
         }
      });
      loadReplyState.observe(lifecycleOwner, aBoolean -> {
         if (aBoolean) {
            performLoading();
         } else {
            finishLoading();
         }
      });
   }

   public void initOnClick(
           CommentSessionHandler commentHandler,
           ReplyListViewModel replyViewModel) {
      viewMoreReply.setOnClickListener(view -> {
         commentHandler.requestSyncData();
         replyViewModel.load(5);
      });
   }

   @Override
   protected void onDetachedFromWindow() {
      if (lifecycleOwner != null) {
         lifecycleOwner.destroy();
      }
      spinnerLoading.setVisibility(GONE);
      super.onDetachedFromWindow();
   }

   private void performLoading() {
      spinnerLoading.setVisibility(VISIBLE);
   }

   private void finishLoading() {
      spinnerLoading.setVisibility(GONE);
   }

   @Override
   protected void onDraw(Canvas canvas) {
      Paint treeStroke = new Paint();
      treeStroke.setColor(Color.parseColor("#dee2e6"));
      treeStroke.setAntiAlias(true);
      treeStroke.setStyle(Paint.Style.STROKE);
      treeStroke.setStrokeWidth(4.5f);

      int x = (int) TypedValue.applyDimension(
              TypedValue.COMPLEX_UNIT_DIP,
              30,
              getContext().getResources().getDisplayMetrics());
      int y = 0;

      Rect rect = new Rect(0, 0, cntReplyTextView.getWidth(), cntReplyTextView.getHeight());
      viewMoreReply.offsetDescendantRectToMyCoords(cntReplyTextView, rect);
      int targetY = (rect.top + rect.bottom) / 2;
      int targetX = rect.left;


      Path path = new Path();
      path.moveTo(x, targetY - 23);
      path.arcTo(x, targetY - 46, x + 46, targetY, -180f, -90f, false);
//
//      path.quadTo(x, targetY, x + 25, targetY);
      path.lineTo(targetX - 10, targetY);
      path.moveTo(x, y);
      path.lineTo(x, targetY - 23);
      canvas.drawPath(path, treeStroke);

      super.onDraw(canvas);
   }
}
