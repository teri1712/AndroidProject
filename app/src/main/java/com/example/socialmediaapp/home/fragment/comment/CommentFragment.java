package com.example.socialmediaapp.home.fragment.comment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.CommentAccessHandler;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.home.fragment.extras.EditTextActionHelper;
import com.example.socialmediaapp.application.repo.core.CommentRepository;
import com.example.socialmediaapp.home.fragment.extras.ExtraViewHolder;
import com.example.socialmediaapp.view.button.UltimateRoundedButton;
import com.example.socialmediaapp.view.layout.CommentGroupManager;
import com.example.socialmediaapp.view.progress.CommentLoading;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.viewmodel.fragment.CommentFragmentViewModel;
import com.example.socialmediaapp.view.adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends Fragment {
  private class CommentLoadingExtra extends RecyclerViewExtra {
    private CommentLoading loading;

    public CommentLoadingExtra() {
      super(new CommentLoading(getContext()), Position.END);
      loading = (CommentLoading) view;
    }

    @Override
    public void configure(View view) {
      LiveData<Boolean> loadState = viewModel.getLoadState();
      loadState.observe(getViewLifecycleOwner(), aBoolean -> {
        if (aBoolean) {
          performLoading();
        } else {
          finishLoading();
        }
      });
    }

    private void performLoading() {
      loading.start();
      loading.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
      loading.cancel();
      loading.setVisibility(View.GONE);
    }
  }

  public static class ScrollConfigurator implements ConfigureExtra {
    private RecyclerView recyclerView;

    @Override
    public void apply(View root, CommentFragmentViewModel commentFragmentViewModel) {
      recyclerView = root.findViewById(R.id.comment_panel);
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

          int pos = linearLayoutManager.findLastVisibleItemPosition();
          int total = recyclerView.getAdapter().getItemCount();

          if (pos + 4 >= total) {
            recyclerView.post(() -> commentFragmentViewModel.load(2));
          }
        }
      });
    }
  }

  public interface ConfigureExtra {
    void apply(View root, CommentFragmentViewModel commentFragmentViewModel);
  }

  public CommentFragment(LiveData<DataAccessHandler<HandlerAccess>> handler
          , List<ConfigureExtra> configs
          , List<RecyclerViewExtra> viewExtra) {
    this.handler = handler;
    this.configs = configs;
    topViews = new ExtraViewHolder(RecyclerViewExtra.Position.START, new ArrayList<>());
    endViews = new ExtraViewHolder(RecyclerViewExtra.Position.START, new ArrayList<>());
    for (RecyclerViewExtra v : viewExtra) {
      if (v.getPos() == RecyclerViewExtra.Position.START) {
        topViews.add(v);
      } else {
        endViews.add(v);
      }
    }
  }

  private ExtraViewHolder topViews, endViews;
  private List<ConfigureExtra> configs;
  private LiveData<DataAccessHandler<HandlerAccess>> handler;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  private RecyclerView recyclerView;
  private CommentFragmentViewModel viewModel;
  private CustomSpinningView spinnerLoading;
  private CommentAdapter adapter;
  private UltimateRoundedButton hintButton;
  private LinearLayoutManager layoutManager;
  private CommentGroupManager commentManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_comment, container, false);
    spinnerLoading = view.findViewById(R.id.load_spinner);
    recyclerView = view.findViewById(R.id.comment_panel);
    hintButton = view.findViewById(R.id.new_comment_button);

    spinnerLoading.setVisibility(View.VISIBLE);
    endViews.add(new CommentLoadingExtra());

    handler.observe(getViewLifecycleOwner(), new Observer<DataAccessHandler<HandlerAccess>>() {
      @Override
      public void onChanged(DataAccessHandler<HandlerAccess> accessHandler) {
        spinnerLoading.setVisibility(View.GONE);
        initViewModel((CommentAccessHandler) accessHandler);
        for (ConfigureExtra config : configs) {
          config.apply(view, viewModel);
        }
      }
    });
    return view;
  }


  private void initRecyclerView() {
    CommentRepository repo = viewModel.getRepo();
    MainCommentFragment mainCommentFragment = (MainCommentFragment) getParentFragment();
    EditTextActionHelper actionHelper = mainCommentFragment.getActionHelper();
    commentManager = new CommentGroupManager(
            getContext(),
            getViewLifecycleOwner(),
            repo,
            actionHelper);
    adapter = new CommentAdapter(commentManager);
    adapter.applyExtraViews(topViews, endViews);
    commentManager.setAdapter(adapter);
    recyclerView.setAdapter(adapter);
    layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
  }

  public CommentFragmentViewModel getViewModel() {
    return viewModel;
  }

  public void initViewModel(CommentAccessHandler accessHandler) {
    viewModel = new CommentFragmentViewModel(accessHandler);
    initRecyclerView();
    LiveData<Boolean> newComment = viewModel.getNewComment();
    newComment.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean aBoolean) {
        if (!aBoolean) {
          hintButton.setVisibility(View.VISIBLE);
          hintButton.setAlpha(0);
          hintButton.animate()
                  .alpha(1)
                  .setDuration(100)
                  .start();
        }
      }
    });
    viewModel.getLoadState()
            .observe(getViewLifecycleOwner(), new Observer<Boolean>() {
              @Override
              public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                  hintButton.setVisibility(View.GONE);
                }
              }
            });
    hintButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        hintButton.setVisibility(View.GONE);
        int total = adapter.getItemCount();
        int pos = layoutManager.findLastCompletelyVisibleItemPosition();

        if (pos + 5 < total) {
          recyclerView.scrollToPosition(total - 5);
        }
        recyclerView.smoothScrollToPosition(total - 1);
      }
    });
    viewModel.load(10);
  }

  public void onDestroyView() {
    commentManager.dispose();
    super.onDestroyView();
  }
}
