package com.example.socialmediaapp.layoutviews.items.message;

import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.lifecycle.LifecycleOwner;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.utils.TimeParserUtil;
import com.example.socialmediaapp.view.container.ClickablePanel;
import com.example.socialmediaapp.home.fragment.message.MessageFragment;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

public class MessageItemView extends ClickablePanel {

   protected LifecycleOwner lifecycleOwner;

   protected LinearLayout root;
   private boolean fixShowTime;
   private boolean timeShown;
   private TextView timeTextView;

   public MessageItemView(MessageFragment owner) {
      super(owner.getContext());
      this.lifecycleOwner = owner.getViewLifecycleOwner();

      timeShown = false;
      init(null);
      setRequestPressPaint(false);

      LayoutInflater inflater = LayoutInflater.from(getContext());
      root = (LinearLayout) inflater.inflate(R.layout.message_item, this, false);
      addView(root);
      initOnClick();
   }

   public void setFixShowTime(final boolean fixShowTime) {
      post(() -> {
         //dump stuff
         MessageItemView.this.fixShowTime = fixShowTime;
         if (fixShowTime) {
            timeTextView.setVisibility(VISIBLE);
         } else {
            timeTextView.setVisibility(GONE);
         }
      });
   }

   public void initViewModel(ChatSessionModel chatSessionModel, MessageItemModel messageItemModel) {

      timeTextView = root.findViewById(R.id.cnt_time);
      long time = messageItemModel.getTime();
      timeTextView.setText(TimeParserUtil.parseTime(time));
   }

   private void initOnClick() {
      setOnClickListener(view -> {
         if (fixShowTime) return;
         if (timeShown) {
            timeTextView.setVisibility(GONE);
         } else {
            timeTextView.setVisibility(VISIBLE);
         }
         timeShown = !timeShown;
      });
   }

}