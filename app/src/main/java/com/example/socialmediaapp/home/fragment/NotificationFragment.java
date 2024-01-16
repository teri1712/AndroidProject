package com.example.socialmediaapp.home.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.application.repo.core.NotificationRepository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.view.adapter.NotificationAdapter;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.viewmodel.NotificationFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends Fragment {

  public class SpinLoadViewExtra extends RecyclerViewExtra {
    private CustomSpinningView loadSpin;

    public SpinLoadViewExtra() {
      super(new CustomSpinningView(getContext()), Position.END);
      loadSpin = (CustomSpinningView) view;
    }

    @Override
    public void configure(View view) {
      loadSpin.setColor(Color.rgb(0x08, 0x66, 0xFF));
      int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25
              , getContext().getResources().getDisplayMetrics());
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(r, r);
      params.gravity = Gravity.CENTER_HORIZONTAL;
      loadSpin.setLayoutParams(params);

      viewModel.getLoadState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
          if (aBoolean) {
            loadSpin.setVisibility(View.VISIBLE);
          } else {
            loadSpin.setVisibility(View.GONE);
          }
        }
      });
    }
  }

  public NotificationFragment() {
  }

  public static NotificationFragment newInstance() {
    NotificationFragment fragment = new NotificationFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private List<RecyclerViewExtra> topViews, endViews;
  private RecyclerView recyclerView;
  private NotificationFragmentViewModel viewModel;
  private SimpleLifecycleOwner lifecycleOwner;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_notification, container, false);
    recyclerView = view.findViewById(R.id.notification_recycler_view);
    lifecycleOwner = new SimpleLifecycleOwner();
    init();
    viewModel.load(10);
    return view;
  }

  public void switchTo() {
    lifecycleOwner.resume();
  }

  public void switchOff() {
    lifecycleOwner.pause();
  }

  private void init() {
    topViews = new ArrayList<>();
    endViews = new ArrayList<>();
    endViews.add(new SpinLoadViewExtra());

    UserSessionViewModel hostViewModel = ((HomePage) getActivity()).getViewModel();
    NotificationRepository repo = hostViewModel.getNotifyRepo();
    viewModel = new NotificationFragmentViewModel(repo);

    NotificationAdapter adapter = new NotificationAdapter(getContext(), repo, topViews, endViews);
    recyclerView.setAdapter(adapter);

    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        int pos = linearLayoutManager.findLastVisibleItemPosition();
        int total = recyclerView.getAdapter().getItemCount();

        if (pos + 2 >= total) {
          viewModel.load(2);
        }
      }
    });
    LiveData<Update> itemUpdate = repo.getItemUpdate();
    itemUpdate.observe(getViewLifecycleOwner(), update -> {
      if (update == null) return;
      Update.Op op = update.op;
      Map<String, Object> data = update.data;
      if (op == Update.Op.ADD) {
        int offset = (int) data.get("offset");
        int length = (int) data.get("length");
        if (length == 0) viewModel.setPaused(true);
        adapter.notifyItemRangeInserted(offset, length);
      }
    });

    LiveData<Integer> countUnRead = repo.getCntUnRead();
    countUnRead.observe(lifecycleOwner, new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        repo.consume();
      }
    });
  }
}