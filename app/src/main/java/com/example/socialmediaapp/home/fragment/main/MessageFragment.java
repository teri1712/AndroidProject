package com.example.socialmediaapp.home.fragment.main;

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
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.MessageAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.layoutviews.items.MessageGroupLayoutManager;
import com.example.socialmediaapp.viewmodel.adapter.MessageAdapter;
import com.example.socialmediaapp.viewmodel.messenger.MessageFragmentViewModel;
import com.example.socialmediaapp.viewmodel.models.messenger.ChatSessionModel;
import com.example.socialmediaapp.viewmodel.models.messenger.MessageItem;
import com.example.socialmediaapp.viewmodel.models.repo.suck.Update;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageFragment extends Fragment {

   public interface ConfigureExtra {
      public void apply(View root, MessageFragmentViewModel messageFragmentViewModel);
   }

   public static class ScrollConfigurator implements ConfigureExtra {
      private RecyclerView recyclerView;

      @Override
      public void apply(View root, MessageFragmentViewModel messageFragmentViewModel) {
         recyclerView = root.findViewById(R.id.message_panel);
         recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
               super.onScrolled(recyclerView, dx, dy);
               LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

               int pos = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
               if (pos <= 4) {
                  messageFragmentViewModel.load(8);
               }
            }
         });
      }
   }

   public class SpinLoadViewExtra extends RecyclerViewExtra {

      private CustomSpinningView loadSpin;

      public SpinLoadViewExtra() {
         super(new CustomSpinningView(getContext()), Position.START);
         loadSpin = (CustomSpinningView) view;
      }

      @Override
      public void configure(View view) {
         loadSpin.setColor(Color.rgb(0x08, 0x66, 0xFF));
         int r = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getContext().getResources().getDisplayMetrics());
         FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(r, r);
         params.gravity = Gravity.CENTER_HORIZONTAL;
         loadSpin.setLayoutParams(params);

         viewModel.getLoadMessageState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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

   public MessageFragment(LiveData<ChatSessionModel> chatSessionModel, List<ConfigureExtra> configureExtras, List<RecyclerViewExtra> viewExtra) {
      this.chatSessionModel = chatSessionModel;
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

   private List<ConfigureExtra> configureExtras;
   private MessageFragmentViewModel viewModel;
   private List<RecyclerViewExtra> topViews, endViews;
   private LiveData<ChatSessionModel> chatSessionModel;
   private MessageAdapter messageAdapter;

   @Override
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
   }

   private RecyclerView recyclerView;
   private CustomSpinningView spinSetup;

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

      View view = inflater.inflate(R.layout.fragment_message, container, false);
      spinSetup = view.findViewById(R.id.load_spinner);
      recyclerView = view.findViewById(R.id.message_panel);
      spinSetup.setVisibility(View.VISIBLE);
      chatSessionModel.observe(getViewLifecycleOwner(), new Observer<ChatSessionModel>() {
         @Override
         public void onChanged(ChatSessionModel chatSessionModel) {
            spinSetup.setVisibility(View.GONE);
            initViewModel(chatSessionModel);
            initRecyclerView();
            for (ConfigureExtra configureExtra : configureExtras)
               configureExtra.apply(view, viewModel);
            viewModel.load(10);
         }
      });
      return view;
   }

   public MessageFragmentViewModel getViewModel() {
      return viewModel;
   }

   private void initRecyclerView() {
      topViews.add(new SpinLoadViewExtra());

      messageAdapter = new MessageAdapter(this, topViews, endViews);
      recyclerView.setAdapter(messageAdapter);
   }

   public void initViewModel(ChatSessionModel chatSessionModel) {
      viewModel = new MessageFragmentViewModel(chatSessionModel);
      LiveData<Update> msgUpdate = viewModel.getMessageRepository().getItemUpdate();

      MessageGroupLayoutManager messageGroupLayoutManager = new MessageGroupLayoutManager();

      msgUpdate.observe(getViewLifecycleOwner(), update -> {
         Update.Op op = update.op;
         assert op == Update.Op.ADD;

         HashMap<String, Object> data = update.data;
         int offset = (int) data.get("offset");
         int length = (int) data.get("length");
         messageAdapter.notifyItemRangeInserted(offset + topViews.size(), length);
      });

   }

}
