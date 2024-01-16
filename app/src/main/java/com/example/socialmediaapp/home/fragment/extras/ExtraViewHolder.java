package com.example.socialmediaapp.home.fragment.extras;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ExtraViewHolder {
  private List<RecyclerViewExtra> viewExtras;
  private RecyclerViewExtra.Position pos;
  private RecyclerView.Adapter adapter;

  public ExtraViewHolder(
          @NonNull RecyclerViewExtra.Position pos,
          @NonNull List<RecyclerViewExtra> viewExtras) {
    this.pos = pos;
    this.viewExtras = viewExtras;
  }

  public void setAdapter(RecyclerView.Adapter adapter) {
    this.adapter = adapter;
  }

  private int calRealPos(int offset) {
    if (adapter == null) return -1;
    if (pos == RecyclerViewExtra.Position.END) {
      int total = adapter.getItemCount();
      int n = viewExtras.size();
      return offset + (total - n);
    }
    return offset;
  }

  public void addAt(int offset, RecyclerViewExtra viewExtra) {
    int rOffset = calRealPos(offset);
    viewExtras.add(offset, viewExtra);
    if (rOffset == -1) return;
    adapter.notifyItemInserted(rOffset);
  }

  public void add(RecyclerViewExtra viewExtra) {
    int offset = viewExtras.size();
    int rOffset = calRealPos(offset);
    viewExtras.add(offset, viewExtra);
    if (rOffset == -1) return;
    adapter.notifyItemInserted(rOffset);
  }

  public void removeAt(int offset) {
    int rOffset = calRealPos(offset);
    viewExtras.remove(offset);
    if (rOffset == -1) return;
    adapter.notifyItemRemoved(rOffset);
  }

  public RecyclerViewExtra getAt(int offset) {
    return viewExtras.get(offset);
  }

  public int length() {
    return viewExtras.size();
  }

}
