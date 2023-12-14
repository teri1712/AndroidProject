package com.example.socialmediaapp.home.fragment.main;

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
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.progress.PostLoading;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.viewmodel.adapter.PostAdapter;
import com.example.socialmediaapp.viewmodel.models.post.base.Post;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
      public void apply(View root, PostFragmentViewModel postFragmentViewModel) {
         recyclerView = root.findViewById(R.id.posts_panel);
         recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

               int pos = linearLayoutManager.findLastVisibleItemPosition();
               int total = recyclerView.getAdapter().getItemCount();

               if (pos + 2 >= total) {
                  postFragmentViewModel.load(2);
               }
            }
         });
      }
   }

   public interface ConfigureExtra {
      void apply(View root, PostFragmentViewModel postFragmentViewModel);
   }

   public PostFragment(LiveData<SessionHandler> handler, List<RecyclerViewExtra> viewExtra, List<ConfigureExtra> configureExtras) {
      this.handler = handler;
      topViews = new ArrayList<>();
      endViews = new ArrayList<>();
      for (RecyclerViewExtra v : viewExtra) {
         if (v.getPos() == RecyclerViewExtra.Position.START) {
            topViews.add(v);
         } else {
            endViews.add(v);
         }
      }
      this.configureExtras = configureExtras;
   }

   private LiveData<SessionHandler> handler;
   private List<RecyclerViewExtra> topViews, endViews;
   private List<ConfigureExtra> configureExtras;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

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

      handler.observe(getViewLifecycleOwner(), sessionHandler -> {
         spinnerLoading.setVisibility(View.GONE);

         initViewModel((DataAccessHandler<Post>) sessionHandler);

         for (ConfigureExtra configureExtra : configureExtras)
            configureExtra.apply(view, viewModel);
      });
      return view;
   }

   private void initRecyclerView() {
      postAdapter = new PostAdapter(this, topViews, endViews);
      postPanel.setAdapter(postAdapter);


      Repository<Post> repository = viewModel.getPostRepository();

      LiveData<Update> itemUpdate = repository.getItemUpdate();

      itemUpdate.observe(getViewLifecycleOwner(), postUpdate -> {
         Update.Op op = postUpdate.op;
         HashMap<String, Object> data = postUpdate.data;
         if (op == Update.Op.ADD) {
            int offset = (int) data.get("offset");
            int length = (int) data.get("length");
            postAdapter.notifyItemRangeInserted(offset + topViews.size(), length);
         } else if (op == Update.Op.REMOVE) {
            int offset = (int) data.get("offset");
            postAdapter.notifyItemRemoved(offset + topViews.size());
         } else if (op == Update.Op.RECYCLE) {
            postAdapter.recycle();
            postAdapter.notifyDataSetChanged();
         }
      });

   }

   @Override
   public void onStart() {
      super.onStart();
   }

   public PostFragmentViewModel getViewModel() {
      return viewModel;
   }

   public void initViewModel(DataAccessHandler<Post> dataAccessHandler) {
      viewModel = new PostFragmentViewModel(dataAccessHandler);
      initRecyclerView();
      viewModel.loadEntrance();

   }

}
