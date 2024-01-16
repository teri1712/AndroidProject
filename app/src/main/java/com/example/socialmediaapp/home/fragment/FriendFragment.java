package com.example.socialmediaapp.home.fragment;

import android.animation.ObjectAnimator;
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
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;

import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.application.repo.core.FriendRequestRepository;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.view.adapter.FriendRequestAdapter;
import com.example.socialmediaapp.view.button.RecCircleButton;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.viewmodel.FriendFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FriendFragment extends Fragment {
  public class SpinLoadFriendRequestViewExtra extends RecyclerViewExtra {
    private CustomSpinningView loadSpin;

    public SpinLoadFriendRequestViewExtra() {
      super(new CustomSpinningView(getContext()), Position.END);
      loadSpin = (CustomSpinningView) view;
    }

    @Override
    public void configure(View view) {
      loadSpin.setColor(Color.rgb(0x08, 0x66, 0xFF));
      int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getContext().getResources().getDisplayMetrics());
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

  public FriendFragment() {
  }

  public static FriendFragment newInstance() {
    FriendFragment fragment = new FriendFragment();
    return fragment;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private RecyclerView fReqRecyclerView, friendRecyclerView;
  private View friendRequestPanel, friendPanel;
  private FriendRequestRepository repo;
  private RecCircleButton friendPageButton, friendRequestPageButton;
  private HorizontalScrollView horizontalScrollView;
  private boolean loadEntranceCalled;

  private FriendFragmentViewModel viewModel;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_friend, container, false);

    horizontalScrollView = view.findViewById(R.id.horizontal_scroll);
    friendRequestPanel = view.findViewById(R.id.friend_request_panel);
    friendPanel = view.findViewById(R.id.friend_panel);

    fReqRecyclerView = view.findViewById(R.id.friend_request_recycler_view);
    friendRecyclerView = view.findViewById(R.id.friend_recycler_view);

    friendPageButton = view.findViewById(R.id.friend_page_button);
    friendRequestPageButton = view.findViewById(R.id.friend_request_page_button);


    initFriendRequestPanel();
    initFriendPanel();
    initOnClick();
    view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        friendPanel.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
        friendRequestPanel.getLayoutParams().width = getResources().getDisplayMetrics().widthPixels;
        friendPanel.requestLayout();
        friendRequestPanel.requestLayout();
      }
    });

    return view;
  }

  public void switchTo() {
    if (!loadEntranceCalled) {
      loadEntranceCalled = true;
      viewModel.load(10);
    }
  }

  private void initFriendRequestPanel() {
    UserSessionViewModel hostViewModel = ((HomePage) getActivity()).getViewModel();
    repo = hostViewModel.getFReqRepo();
    viewModel = new FriendFragmentViewModel(repo);
    viewModel.getLoadState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean aBoolean) {

      }
    });

    List<RecyclerViewExtra> endViews = new ArrayList<>();
    endViews.add(new SpinLoadFriendRequestViewExtra());

    FriendRequestAdapter adapter = new FriendRequestAdapter(getContext(), repo, new ArrayList<>(), endViews);
    fReqRecyclerView.setAdapter(adapter);

    fReqRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
        adapter.notifyItemRangeInserted(offset, length);
      } else if (op == Update.Op.REMOVE) {
        int offset = (int) data.get("offset");
        adapter.notifyItemRemoved(offset);
      }
    });
  }

  private void initFriendPanel() {

  }

  private void initOnClick() {
    friendPageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ObjectAnimator animator = ObjectAnimator.ofInt(horizontalScrollView, "scrollX", getResources().getDisplayMetrics().widthPixels);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(100);
        animator.start();
        friendRequestPageButton.setTextContentColor(Color.BLACK);
        friendPageButton.setTextContentColor(Color.parseColor("#0866FF"));

        friendRequestPageButton.setBackgroundColor(Color.parseColor("#0F000000"));
        friendPageButton.setBackgroundColor(Color.parseColor("#140866FF"));
      }
    });
    friendRequestPageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ObjectAnimator animator = ObjectAnimator.ofInt(horizontalScrollView, "scrollX", getResources().getDisplayMetrics().widthPixels);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(100);
        animator.start();
        friendPageButton.setTextContentColor(Color.BLACK);
        friendRequestPageButton.setTextContentColor(Color.parseColor("#0866FF"));

        friendPageButton.setBackgroundColor(Color.parseColor("#0F000000"));
        friendRequestPageButton.setBackgroundColor(Color.parseColor("#140866FF"));
      }
    });
  }
}