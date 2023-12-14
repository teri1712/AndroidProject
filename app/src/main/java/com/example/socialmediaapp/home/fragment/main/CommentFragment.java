package com.example.socialmediaapp.home.fragment.main;

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
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.progress.CommentLoading;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.adapter.CommentAdapter;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentFragment extends Fragment {
    private class CommentLoadingExtra extends RecyclerViewExtra {
        private CommentLoading commentLoading;

        public CommentLoadingExtra() {
            super(new CommentLoading(getContext()), Position.END);
            commentLoading = (CommentLoading) view;
        }

        @Override
        public void configure(View view) {
            LiveData<Boolean> loadState = viewModel.getLoadCommentState();
            loadState.observe(getViewLifecycleOwner(), aBoolean -> {
                if (aBoolean) {
                    performLoading();
                } else {
                    finishLoading();
                }
            });
        }

        private void performLoading() {
            commentLoading.start();
            commentLoading.setVisibility(View.VISIBLE);
        }

        private void finishLoading() {
            commentLoading.cancel();
            commentLoading.setVisibility(View.GONE);
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

                    if (pos + 2 >= total) {
                        commentFragmentViewModel.load(2);
                    }
                }
            });
        }
    }

    public interface ConfigureExtra {
        void apply(View root, CommentFragmentViewModel commentFragmentViewModel);
    }

    public CommentFragment(LiveData<SessionHandler> handler, List<ConfigureExtra> configureExtras, List<RecyclerViewExtra> viewExtra) {
        this.handler = handler;
        this.configureExtras = configureExtras;
        topViews = new ArrayList<>();
        endViews = new ArrayList<>();
        for (RecyclerViewExtra v : viewExtra) {
            if (v.getPos() == RecyclerViewExtra.Position.START) {
                topViews.add(v);
            } else {
                endViews.add(v);
            }
        }

    }

    private List<RecyclerViewExtra> topViews, endViews;
    private List<ConfigureExtra> configureExtras;
    private LiveData<SessionHandler> handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private RecyclerView commentPanel;
    private CommentFragmentViewModel viewModel;
    private CustomSpinningView spinnerLoading;
    private CommentAdapter commentAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        spinnerLoading = view.findViewById(R.id.load_spinner);
        spinnerLoading.setVisibility(View.VISIBLE);
        commentPanel = view.findViewById(R.id.comment_panel);
        endViews.add(new CommentLoadingExtra());

        handler.observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                spinnerLoading.setVisibility(View.GONE);
                initViewModel((DataAccessHandler<Comment>) sessionHandler);
                for (ConfigureExtra configureExtra : configureExtras) {
                    configureExtra.apply(view, viewModel);
                }
            }
        });

        return view;
    }

    private void initRecyclerView() {
        commentAdapter = new CommentAdapter(this, topViews, endViews);
        commentPanel.setAdapter(commentAdapter);

        Repository<Comment> repository = viewModel.getCommentRepository();
        LiveData<Update> itemUpdate = repository.getItemUpdate();
        itemUpdate.observe(getViewLifecycleOwner(), update -> {
            Update.Op op = update.op;
            HashMap<String, Object> data = update.data;
            if (op == Update.Op.ADD) {
                int offset = (int) data.get("offset");
                int length = (int) data.get("length");
                commentAdapter.notifyItemRangeInserted(offset + topViews.size(), length);
            } else if (op == Update.Op.REMOVE) {
                int offset = (int) data.get("offset");
                commentAdapter.notifyItemRemoved(offset + topViews.size());
            } else if (op == Update.Op.RECYCLE) {
                commentAdapter.notifyDataSetChanged();
            }
        });

    }

    public CommentFragmentViewModel getViewModel() {
        return viewModel;
    }

    public void initViewModel(DataAccessHandler<Comment> dataAccessHandler) {
        viewModel = new CommentFragmentViewModel(dataAccessHandler);
        initRecyclerView();
        viewModel.loadEntrance();
    }


    public void onDestroyView() {
        super.onDestroyView();
    }
}
