package com.example.socialmediaapp.view.layout;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.ReplySessionHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.home.fragment.extras.EditTextActionHelper;
import com.example.socialmediaapp.layoutviews.items.CommentItemView;
import com.example.socialmediaapp.layoutviews.items.ReplyCommentItemView;
import com.example.socialmediaapp.layoutviews.items.ViewReplyItem;
import com.example.socialmediaapp.application.repo.core.ReplyCommentRepository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.view.adapter.CommentAdapter;
import com.example.socialmediaapp.viewmodel.fragment.ReplyListViewModel;

import java.util.Map;

public class CommentGroup {
  private LifecycleOwner lifecycleOwner;
  private EditTextActionHelper actionHelper;
  private ReplyListViewModel replyListViewModel;
  private CommentSessionHandler handler;
  private ReplyCommentRepository replyRepo;
  private CommentGroupManager groupManager;
  private int pos;

  public CommentGroup(
          @NonNull LifecycleOwner lifecycleOwner,
          @NonNull HandlerAccess handlerAccess,
          @NonNull EditTextActionHelper actionHelper) {
    this.lifecycleOwner = lifecycleOwner;
    this.actionHelper = actionHelper;
    this.handler = (CommentSessionHandler) handlerAccess.access();
    initViewModel();
  }

  public void applyCommentPositionModel(
          CommentGroupManager groupManager, int pos) {
    this.groupManager = groupManager;
    this.pos = pos;
  }

  private void initViewModel() {
    replyListViewModel = new ReplyListViewModel(handler.getReplyDataAccess());
    replyRepo = replyListViewModel.getReplyRepo();

    LiveData<Update> replyUpdate = replyRepo.getItemUpdate();

    replyUpdate.observe(lifecycleOwner, update -> {
      if (update == null) return;

      Map<String, Object> data = update.data;
      Integer offset = (Integer) data.get("offset");
      Integer length = (Integer) data.get("length");
      if (length == 0) return;
      if (replyRepo.length() != 0) {
        groupManager.onReplyViewChanged(pos, replyRepo.length() - 1);
      }
      groupManager.onReplyInserted(pos, offset + 1, length);
    });
  }

  private boolean isCommentPos(int pos) {
    return pos == 0;
  }

  private boolean isReplyPos(int pos) {
    return pos > 0 && pos <= replyRepo.length();
  }

  public int getViewTypeAt(int pos) {
    if (isCommentPos(pos)) {
      return CommentAdapter.COMMENT_VIEW;
    } else if (isReplyPos(pos)) {
      return CommentAdapter.REPLY_VIEW;
    }
    return CommentAdapter.LOAD_VIEW;
  }

  public void applyViewModel(View view, int pos) {
    if (isCommentPos(pos)) {
      CommentItemView commentView = (CommentItemView) view;
      commentView.initViewModel(handler);
      commentView.setReplyButtonAction(() -> {
        String name = handler.getCommentData().getValue().getAuthor().getFullname();
        actionHelper.setActionOnEditText(name, replyListViewModel::upload);
      });
      return;
    }
    if (isReplyPos(pos)) {
      ReplySessionHandler replyHandler = replyRepo.get(pos - 1).access();
      ReplyCommentItemView replyView = (ReplyCommentItemView) view;
      replyView.initViewModel(replyHandler);
      replyView.setLastReply(replyRepo.length() == pos);
      return;
    }
    ViewReplyItem viewReplyItem = (ViewReplyItem) view;

    LiveData<Boolean> loadReplyState = replyListViewModel.getLoadState();
    LiveData<Integer> countUnReadReply = replyListViewModel.getCountUnRead();
    viewReplyItem.initViewModel(loadReplyState, countUnReadReply);
    viewReplyItem.initOnClick(handler, replyListViewModel);
  }
  public void close() {
    replyRepo.close();
  }
}
