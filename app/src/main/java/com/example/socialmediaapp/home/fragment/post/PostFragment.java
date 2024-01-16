package com.example.socialmediaapp.home.fragment.post;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.home.fragment.extras.ExtraViewHolder;
import com.example.socialmediaapp.view.progress.PostLoading;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.view.adapter.PostAdapter;
import com.example.socialmediaapp.application.repo.core.utilities.Update;
import com.example.socialmediaapp.viewmodel.fragment.PostFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PostFragment extends Fragment {

  private class PostLoadingExtra extends RecyclerViewExtra {
    private PostLoading postLoading;

    public PostLoadingExtra() {
      super(new PostLoading(getContext()), Position.END);
      postLoading = (PostLoading) view;
    }

    @Override
    public void configure(View view) {
      LiveData<Boolean> postLoadState = viewModel.getLoadPostState();
      postLoadState.observe(getViewLifecycleOwner(), aBoolean -> {
        if (aBoolean) {
          performLoading();
        } else {
          finishLoading();
        }
      });
    }

    private void performLoading() {
      postLoading.start();
      postLoading.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
      postLoading.cancel();
      postLoading.setVisibility(View.GONE);
    }
  }

  public static class ScrollConfigurator implements ConfigureExtra {
    private RecyclerView recyclerView;

    @Override
    public void apply(View root, PostFragmentViewModel fragmentViewModel) {
      recyclerView = root.findViewById(R.id.posts_panel);
      recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

          int pos = linearLayoutManager.findLastVisibleItemPosition();
          int total = recyclerView.getAdapter().getItemCount();

          if (pos + 4 >= total) {
            recyclerView.post(() -> fragmentViewModel.load(2));
          }
        }
      });
    }
  }

  public interface ConfigureExtra {
    void apply(View root, PostFragmentViewModel fragmentViewModel);
  }

  public PostFragment(Repository<HandlerAccess> repo,
                      List<RecyclerViewExtra> viewExtra,
                      List<ConfigureExtra> configureExtras) {
    this.repo = repo;
    this.topViews = new ExtraViewHolder(RecyclerViewExtra.Position.START, new ArrayList<>());
    this.endViews = new ExtraViewHolder(RecyclerViewExtra.Position.END, new ArrayList<>());
    for (RecyclerViewExtra v : viewExtra) {
      if (v.getPos() == RecyclerViewExtra.Position.START) {
        topViews.add(v);
      } else {
        endViews.add(v);
      }
    }
    this.configureExtras = configureExtras;
  }

  private Repository<HandlerAccess> repo;
  private ExtraViewHolder topViews, endViews;
  private List<ConfigureExtra> configureExtras;
  private RecyclerView postPanel;
  private PostFragmentViewModel viewModel;
  private CustomSpinningView spinnerLoading;
  private PostAdapter postAdapter;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_posts, container, false);
    spinnerLoading = view.findViewById(R.id.load_spinner);
    spinnerLoading.setVisibility(View.VISIBLE);
    postPanel = view.findViewById(R.id.posts_panel);
    endViews.add(new PostLoadingExtra());

    initViewModel(repo);

    spinnerLoading.setVisibility(View.GONE);


    for (ConfigureExtra configureExtra : configureExtras)
      configureExtra.apply(view, viewModel);
    return view;
  }

  private void initRecyclerView() {
    Repository<HandlerAccess> repo = viewModel.getPostRepo();
    postAdapter = new PostAdapter(repo);
    postAdapter.applyExtraViews(topViews, endViews);
    postPanel.setAdapter(postAdapter);

    LiveData<Update> itemUpdate = repo.getItemUpdate();

    itemUpdate.observe(getViewLifecycleOwner(), postUpdate -> {
      if (postUpdate == null) return;

      Update.Op op = postUpdate.op;
      Map<String, Object> data = postUpdate.data;
      if (op == Update.Op.ADD) {
        int offset = (int) data.get("offset");
        int length = (int) data.get("length");
        postAdapter.notifyViewRangeInserted(offset, length);
        if (length == 0) {
          viewModel.getPaused().setValue(true);
        }
      } else if (op == Update.Op.RECYCLE) {
        postAdapter.notifyDataSetChanged();
        viewModel.load(5);
      }
    });
  }

  public PostFragmentViewModel getViewModel() {
    return viewModel;
  }

  public void initViewModel(Repository<HandlerAccess> repo) {
    viewModel = new PostFragmentViewModel(repo);
    initRecyclerView();
    viewModel.getPaused().observe(getViewLifecycleOwner(), aBoolean -> {
      if (!aBoolean) {
        viewModel.load(5);
      }
    });
  }
}
