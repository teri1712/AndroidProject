package com.example.socialmediaapp.view.layout;

import android.content.Context;
import android.view.View;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.example.socialmediaapp.home.fragment.extras.EditTextActionHelper;
import com.example.socialmediaapp.application.repo.core.CommentRepository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.view.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CommentGroupManager {
   private Context context;
   private LifecycleOwner lifecycleOwner;
   private EditTextActionHelper actionHelper;
   private CommentAdapter adapter;
   private CommentRepository repo;
   private Fenwick inOrder;
   private Fenwick rOrder;

   public CommentGroupManager(
           Context context,
           LifecycleOwner lifecycleOwner,
           CommentRepository repo,
           EditTextActionHelper actionHelper) {
      this.context = context;
      this.lifecycleOwner = lifecycleOwner;
      this.actionHelper = actionHelper;
      this.inOrder = new Fenwick();
      this.rOrder = new Fenwick();
      this.repo = repo;

      initUpdateListen();
   }

   private void initUpdateListen() {
      LiveData<Update> itemUpdate = repo.getItemUpdate();
      itemUpdate.observe(lifecycleOwner, update -> {
         if (update == null) return;

         Update.Op op = update.op;
         Map<String, Object> data = update.data;
         if (op == Update.Op.ADD) {
            int offset = (int) data.get("offset");
            int length = (int) data.get("length");
            List<CommentGroup> commentGroups = new ArrayList<>();
            for (int i = 0; i < length; i++) {
               CommentGroup commentGroup = new CommentGroup(
                       lifecycleOwner,
                       repo.get(offset + i),
                       actionHelper
               );
               commentGroups.add(commentGroup);
            }
            insert(commentGroups, offset != 0);
         }
      });
   }
   public void setAdapter(CommentAdapter adapter) {
      this.adapter = adapter;
   }

   protected void onReplyInserted(int pos, int offset, int length) {
      int aPos = findActualPosOfComment(pos);
      if (pos >= 0) {
         inOrder.update(pos, length);
      } else {
         rOrder.update(-pos - 1, length);
      }
      adapter.notifyViewRangeInserted(aPos + offset, length);
   }

   protected void onReplyViewChanged(int pos, int offset) {
      int aPos = findActualPosOfComment(pos);
      adapter.notifyViewChanged(aPos + offset, 1);
   }

   public int getViewType(int aPos) {
      int pos;
      if (aPos >= rOrder.total) {
         pos = inOrder.findLowerBound((aPos + 1) - rOrder.total) + rOrder.n;
      } else {
         int upper = rOrder.findUpperBound(rOrder.total - (aPos + 1));
         pos = Math.max(-upper - 1, -rOrder.n);
      }
      CommentGroup commentGroup = pos < 0
              ? rOrder.comments.get(pos + 1)
              : inOrder.comments.get(pos + rOrder.n);
      int offset = aPos - findActualPosOfComment(pos);
      return commentGroup.getViewTypeAt(offset);
   }

   public void applyViewModel(int aPos, View view) {
      int pos;
      if (aPos >= rOrder.total) {
         pos = inOrder.findLowerBound((aPos + 1) - rOrder.total) + rOrder.n;
      } else {
         int upper = rOrder.findUpperBound(rOrder.total - (aPos + 1));
         pos = Math.max(-upper - 1, -rOrder.n);
      }
      CommentGroup commentGroup = pos < 0
              ? rOrder.comments.get(pos + 1)
              : inOrder.comments.get(pos + rOrder.n);
      int offset = aPos - findActualPosOfComment(pos);
      commentGroup.applyViewModel(view, offset);
   }

   public void insert(List<CommentGroup> commentGroups, boolean end) {
      if (end) {
         for (CommentGroup commentGroup : commentGroups) {
            addEnd(commentGroup);
         }
      } else {
         Collections.reverse(commentGroups);
         for (CommentGroup commentGroup : commentGroups) {
            addBeg(commentGroup);
         }
      }
   }

   private void addBeg(CommentGroup commentGroup) {
      rOrder.pushEnd(commentGroup);
      commentGroup.applyCommentPositionModel(this, -rOrder.n);
      adapter.notifyViewRangeInserted(0, 2);
   }

   private void addEnd(CommentGroup commentGroup) {
      inOrder.pushEnd(commentGroup);
      commentGroup.applyCommentPositionModel(this, inOrder.n - 1);
      adapter.notifyViewRangeInserted(length() - 2, 2);
   }

   public int length() {
      return inOrder.total + rOrder.total;
   }

   private int findActualPosOfComment(int pos) {
      if (pos < 0) {
         return rOrder.total - rOrder.get(-pos - 1);
      }
      return inOrder.get(pos - 1) + rOrder.total;
   }

   public void dispose() {
      for (CommentGroup commentGroup : rOrder.comments) {
         commentGroup.close();
      }
      for (CommentGroup commentGroup : inOrder.comments) {
         commentGroup.close();
      }
      repo.close();
   }


   /* Fenwick Tree (BIT) data structure, all method below O(log(n))*/
   private class Fenwick {
      private int total;
      private List<CommentGroup> comments;
      private List<Integer> bit;
      private List<Integer> values;
      private int n;

      private Fenwick() {
         this.comments = new ArrayList<>();
         this.bit = new ArrayList<>();
         this.values = new ArrayList<>();
         this.total = 0;
         this.n = 0;

         bit.add(0);
         values.add(0);
      }
      public void update(int pos, int val) {
         total += val;
         pos++;
         values.set(pos, values.get(pos) + val);
         while (pos <= n) {
            bit.set(pos, bit.get(pos) + val);
            pos += pos & -pos;
         }
      }

      public int get(int pos) {
         assert pos < n;
         pos++;
         int sum = 0;
         while (pos > 0) {
            sum += bit.get(pos);
            pos -= pos & -pos;
         }
         return sum;
      }

      private int findLowerBound(int val) {
         int mLog = (int) (Math.log(n) / Math.log(2));
         int cur = 0;
         for (int e = mLog; e >= 0; e--) {
            int nxt = cur + (1 << e);
            if (nxt > n) continue;
            if (bit.get(nxt) < val) {
               cur += (1 << e);
               val -= (1 << e);
            }
         }
         return cur;
      }

      private int findUpperBound(int val) {
         int mLog = (int) (Math.log(n) / Math.log(2));
         int cur = 0;
         for (int e = mLog; e >= 0; e--) {
            int nxt = cur + (1 << e);
            if (nxt > n) continue;
            if (bit.get(nxt) <= val) {
               cur += (1 << e);
               val -= (1 << e);
            }
         }
         return cur;
      }

      private void pushEnd(CommentGroup c) {
         comments.add(c);
         int val = 2;

         total += val;
         values.add(val);
         n++;
         for (int i = n - 1; i > n - (n & -n); i--) {
            val += values.get(i);
         }
         bit.add(val);
      }
   }
}
