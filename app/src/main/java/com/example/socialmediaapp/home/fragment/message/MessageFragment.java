package com.example.socialmediaapp.home.fragment.message;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.layoutviews.items.message.TextingItemView;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.view.adapter.MessageAdapter;
import com.example.socialmediaapp.viewmodel.messenger.MessageFragmentViewModel;
import com.example.socialmediaapp.models.messenger.chat.ChatSessionModel;
import com.example.socialmediaapp.application.repo.core.utilities.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment {
  public class TextingViewExtra extends RecyclerViewExtra {
    private TextingItemView textingView;

    public TextingViewExtra() {
      super(new TextingItemView(getContext()), Position.START);
      textingView = (TextingItemView) view;
    }

    @Override
    public void configure(View view) {
      ChatSessionModel chatModel = MessageFragment.this.chatModel.getValue();
      textingView.init(chatModel.getOther());
      LiveData<Boolean> isTexting = chatModel.getOnlineChat().getIsTexting();
      isTexting.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
        @Override
        public void onChanged(Boolean aBoolean) {
          if (aBoolean) {
            textingView.setVisibility(View.VISIBLE);
          } else {
            textingView.setVisibility(View.GONE);
          }
        }
      });
    }
  }

  public interface ConfigureExtra {
    public void apply(View root, MessageFragmentViewModel fragmentViewModel);
  }

  public static class ScrollConfigurator implements ConfigureExtra {
    private RecyclerView recyclerView;
    private RecyclerView.OnScrollListener onScrollListener;

    @Override
    public void apply(View root, MessageFragmentViewModel fragmentViewModel) {
      recyclerView = root.findViewById(R.id.message_panel);
      onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
          super.onScrolled(recyclerView, dx, dy);
          LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
          int pos = linearLayoutManager.findLastCompletelyVisibleItemPosition();
          int total = recyclerView.getAdapter().getItemCount();
          if (pos + 4 >= total) {
            fragmentViewModel.load(8);
          }
        }
      };
      recyclerView.addOnScrollListener(onScrollListener);
    }

    private void cancel() {
      recyclerView.removeOnScrollListener(onScrollListener);
    }
  }

  public class SpinLoadViewExtra extends RecyclerViewExtra {
    private CustomSpinningView loadSpin;

    public SpinLoadViewExtra() {
      super(new CustomSpinningView(getContext()), Position.END);
      loadSpin = (CustomSpinningView) view;
    }

    @Override
    public void configure(View view) {
      loadSpin.setColor(Color.rgb(0x08, 0x66, 0xFF));
      int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
              25,
              getContext().getResources().getDisplayMetrics());
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

  public MessageFragment(LiveData<ChatSessionModel> chatModel,
                         List<ConfigureExtra> configs,
                         List<RecyclerViewExtra> viewExtra) {
    this.chatModel = chatModel;
    this.configureExtras = configs;
    configs.add(scrollConfigurator);
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

  private ScrollConfigurator scrollConfigurator = new ScrollConfigurator();
  private List<ConfigureExtra> configureExtras;
  private MessageFragmentViewModel viewModel;
  private List<RecyclerViewExtra> topViews, endViews;
  private LiveData<ChatSessionModel> chatModel;
  private MessageAdapter adapter;
  private RecyclerView recyclerView;
  private CustomSpinningView spinSetup;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_message, container, false);
    spinSetup = view.findViewById(R.id.load_spinner);
    recyclerView = view.findViewById(R.id.message_panel);
    spinSetup.setVisibility(View.VISIBLE);
    chatModel.observe(getViewLifecycleOwner(), new Observer<ChatSessionModel>() {
      @Override
      public void onChanged(ChatSessionModel chatSessionModel) {
        spinSetup.setVisibility(View.GONE);
        initViewModel(chatSessionModel);
        initRecyclerView();
        viewModel.load(10);
        for (ConfigureExtra configureExtra : configureExtras)
          configureExtra.apply(view, viewModel);
      }
    });
    return view;
  }

  public MessageFragmentViewModel getViewModel() {
    return viewModel;
  }

  private void initRecyclerView() {
    topViews.add(new TextingViewExtra());
    endViews.add(new SpinLoadViewExtra());
    adapter = new MessageAdapter(this, topViews, endViews);
    recyclerView.setAdapter(adapter);
    LiveData<Update> msgUpdate = viewModel.getRepo().getItemUpdate();

    msgUpdate.observe(getViewLifecycleOwner(), update -> {
      if (update == null) return;
      Update.Op op = update.op;
      assert op == Update.Op.ADD;
      Map<String, Object> data = update.data;
      int offset = (int) data.get("offset");
      int length = (int) data.get("length");
      if (length == 0) {
        scrollConfigurator.cancel();
        return;
      }
      if (offset == 0) {
        recyclerView.smoothScrollToPosition(0);
      }
      adapter.notifyViewRangeInserted(offset, length);
    });
  }

  public void initViewModel(ChatSessionModel chatSessionModel) {
    viewModel = new MessageFragmentViewModel(chatSessionModel.getChatHandler().getAccessHandler());
  }

  @Override
  public void onDestroy() {
    viewModel.getRepo().close();
    super.onDestroy();
  }
}
