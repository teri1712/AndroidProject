package com.example.socialmediaapp.layoutviews.items.message.layout;

import com.example.socialmediaapp.layoutviews.items.message.MessageItemView;
import com.example.socialmediaapp.view.message.RoundedClipMessage;
import com.example.socialmediaapp.models.messenger.message.base.MessageItemModel;

import java.util.ArrayList;
import java.util.List;

public class MessageGroupLayoutManager {
  final long mUnit = 1000 * 60;

  public static class MessageViewSource {
    private MessageItemModel messageItemModel;
    private MessageItemView messageItemView;

    public MessageViewSource(MessageItemModel messageItemModel, MessageItemView messageItemView) {
      this.messageItemModel = messageItemModel;
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
        PositionedItem positionedItem = (PositionedItem) messageItemViews.get(i).messageItemView;
        int pos = 0;
        if (i != 0 && i != messageItemViews.size() - 1) {
          pos = RoundedClipMessage.MIDDLE;
        }
        if (i == 0) {
          pos |= RoundedClipMessage.START;
        }
        if (i == messageItemViews.size() - 1) {
          pos |= RoundedClipMessage.END;
        }
        positionedItem.onPositionChanged(pos);
      }
    }
  }

  private List<MessageGroup> messageGroups;
  private int topMost = -1;
  private ArrayList<MessageViewSource> cacheForMerge;
  private MessageViewSource topView, endView;

  public MessageGroupLayoutManager() {
    messageGroups = new ArrayList<>();
    cacheForMerge = new ArrayList<>();
  }

  public void applyNewItem(MessageViewSource messageViewSource, int offset) {
    if (messageGroups.isEmpty()) {
      messageGroups.add(new MessageGroup());
    }
    if (offset == topMost + 1) {
      cacheForMerge.add(messageViewSource);
      topMost += cacheForMerge.size();
      for (int i = cacheForMerge.size() - 1; i >= 0; i--) {
        MessageViewSource msg = cacheForMerge.get(i);
        pushTop(msg);
        if (topView != null) {
          long dif = topView.messageItemModel.getTime() - msg.messageItemModel.getTime();
          topView.messageItemView.setFixShowTime(dif >= 2 * mUnit);
        }
        if (endView == null) endView = msg;
        topView = msg;
        topView.messageItemView.setFixShowTime(true);
      }
      cacheForMerge.clear();
    } else {
      if (offset != 0) {
        cacheForMerge.add(messageViewSource);
      } else {
        pushEnd(messageViewSource);
        if (endView != null) {
          long dif = messageViewSource.messageItemModel.getTime() - endView.messageItemModel.getTime();
          messageViewSource.messageItemView.setFixShowTime(dif >= 2 * mUnit);
        }
        if (topView == null) topView = messageViewSource;
        endView = messageViewSource;
        topMost++;
      }
    }
  }

  private void pushTop(MessageViewSource msg) {
    MessageGroup msgGroup = messageGroups.get(0);
    List<MessageViewSource> listMsg = msgGroup.messageItemViews;
    if (!(msg.messageItemView instanceof PositionedItem)) {
      messageGroups.add(0, new MessageGroup());
      return;
    } else if (!listMsg.isEmpty()) {
      long timeDif = topView.messageItemModel.getTime() - msg.messageItemModel.getTime();
      if (timeDif >= mUnit * 2 || msgGroup.messageItemViews.get(0).messageItemModel.isMine() != msg.messageItemModel.isMine()) {
        msgGroup = new MessageGroup();
        messageGroups.add(0, msgGroup);
      }
    }
    msgGroup.add(msg, 0);
  }

  private void pushEnd(MessageViewSource msg) {

    MessageGroup msgGroup = messageGroups.get(messageGroups.size() - 1);
    List<MessageViewSource> listMsg = msgGroup.messageItemViews;
    if (!(msg.messageItemView instanceof PositionedItem)) {
      messageGroups.add(new MessageGroup());
      return;
    } else if (!listMsg.isEmpty()) {
      long timeDif = msg.messageItemModel.getTime() - endView.messageItemModel.getTime();
      if (timeDif >= mUnit * 2
              || listMsg.get(listMsg.size() - 1)
              .messageItemModel.isMine() != msg.messageItemModel.isMine()) {
        msgGroup = new MessageGroup();
        messageGroups.add(msgGroup);
      }
    }

    msgGroup.add(msg, -1);
  }
}
