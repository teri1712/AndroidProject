package com.example.socialmediaapp.layoutviews.items;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.message.ClippedMessageView;
import com.example.socialmediaapp.viewmodel.messenger.MessageGroupViewModel;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageGroup;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageGroupLayoutManager {
   final long mUnit = 1000 * 60;

   public static class MessageViewSource {
      private long time;
      private MessageItemView messageItemView;

      public MessageViewSource(long time, MessageItemView messageItemView) {
         this.time = time;
         this.messageItemView = messageItemView;
      }

   }

   private class MessageGroup {
      private List<MessageViewSource> messageItemViews;

      public MessageGroup() {
         messageItemViews = new ArrayList<>();
      }

      public void add(MessageViewSource messageItemView, int offset) {
         if (offset == 0) {
            messageItemViews.add(0, messageItemView);
         } else {
            messageItemViews.add(messageItemView);
         }
         relayout();
      }

      private void relayout() {
         for (int i = 0; i < messageItemViews.size(); i++) {
            MessageItemView messageItemView = messageItemViews.get(i).messageItemView;
            ClippedMessageView clippedMessageView = messageItemView.findViewById(R.id.msg_clipped_view);
            int pos = 0;
            if (i != 0 && i != messageItemViews.size() - 1) {
               pos = ClippedMessageView.MIDDLE;
            }
            if (i == 0) {
               pos |= ClippedMessageView.START;
            }
            if (i == messageItemViews.size() - 1) {
               pos |= ClippedMessageView.END;
            }
            clippedMessageView.setPosition(pos);
            messageItemView.setFixShowTime(i == 0);
         }
      }
   }

   private List<MessageGroup> messageGroups;

   public MessageGroupLayoutManager() {
      messageGroups = new ArrayList<>();
   }

   public void applyNewItem(MessageViewSource messageViewSource, int offset) {
      if (messageGroups.isEmpty()) {
         messageGroups.add(new MessageGroup());
      }
      if (offset == 0) {
         pushTop(messageViewSource);
      } else {
         pushEnd(messageViewSource);
      }
   }

   private void pushTop(MessageViewSource msg) {
      MessageGroup msgGroup = messageGroups.get(0);
      List<MessageViewSource> listMsg = msgGroup.messageItemViews;
      if (listMsg.isEmpty()) {
         msgGroup.add(msg, 0);
         return;
      }

      long startTime = listMsg.get(0).time;
      long timeDif = startTime - msg.time;
      if (timeDif >= mUnit * 2) {
         msgGroup = new MessageGroup();
         messageGroups.add(0, msgGroup);
      }
      msgGroup.add(msg, 0);
   }

   private void pushEnd(MessageViewSource msg) {
      MessageGroup msgGroup = messageGroups.get(messageGroups.size() - 1);

      List<MessageViewSource> listMsg = msgGroup.messageItemViews;
      if (listMsg.isEmpty()) {
         msgGroup.add(msg, -1);
         return;
      }

      long startTime = listMsg.get(0).time;
      long timeDif = startTime - msg.time;
      if (timeDif >= mUnit * 2) {
         msgGroup = new MessageGroup();
         messageGroups.add(-1, msgGroup);
      }
      msgGroup.add(msg, -1);
   }


}
