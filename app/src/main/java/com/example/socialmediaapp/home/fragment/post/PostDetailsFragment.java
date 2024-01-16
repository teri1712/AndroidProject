package com.example.socialmediaapp.home.fragment.post;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.application.repo.core.UploadAdapter;
import com.example.socialmediaapp.application.session.CommentSessionHandler;
import com.example.socialmediaapp.application.session.CommentUploadTask;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.PostHandlerStore;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.home.fragment.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.extras.EditTextActionHelper;
import com.example.socialmediaapp.home.fragment.extras.ExtraViewHolder;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.application.repo.core.CommentRepository;
import com.example.socialmediaapp.application.repo.core.Repository;
import com.example.socialmediaapp.view.adapter.CommentAdapter;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.button.UltimateRoundedButton;
import com.example.socialmediaapp.view.layout.CommentGroup;
import com.example.socialmediaapp.view.layout.CommentGroupManager;
import com.example.socialmediaapp.view.progress.CommentLoading;
import com.example.socialmediaapp.view.progress.SendCommentloading;
import com.example.socialmediaapp.view.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.fragment.PostDetailsFragmentViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class PostDetailsFragment extends Fragment implements FragmentAnimation {


  public class SendingCommentViewExtra extends RecyclerViewExtra {
    private SendCommentloading sendCommentloading;

    public SendingCommentViewExtra() {
      super(new SendCommentloading(getContext()), Position.START);
      sendCommentloading = (SendCommentloading) view;
    }

    @Override
    public void configure(View view) {

      UserSessionViewModel userSessionViewModel = ((HomePage) getActivity()).getViewModel();
      userSessionViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
        @Override
        public void onChanged(Bitmap bitmap) {
          Drawable avatar = bitmap == null ? null : new BitmapDrawable(getResources(), bitmap);
          sendCommentloading.setAvatar(avatar);
        }
      });
      userSessionViewModel.getFullname().observe(getViewLifecycleOwner(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          sendCommentloading.setFullname(s);
        }
      });
      viewModel.getSendState().observe(getViewLifecycleOwner(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          if (s.equals("Idle")) {
            finishSending();
          } else if (s.equals("Sending")) {
            performSending();
          }
        }
      });

      LiveData<Uri> imageUri = viewModel.getImage();
      LiveData<String> content = viewModel.getContent();
      LiveData<String> sending = viewModel.getSendState();
      sending.observe(getViewLifecycleOwner(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          if (s.equals("Sending")) {
            sendCommentloading.setLoadingContent(content.getValue(), imageUri.getValue());
          }
        }
      });
    }

    private void performSending() {
      sendCommentloading.setVisibility(View.VISIBLE);
    }

    private void finishSending() {
      sendCommentloading.setVisibility(View.GONE);
    }
  }

  private class CommentLoadingExtra extends RecyclerViewExtra {
    private CommentLoading commentLoading;

    public CommentLoadingExtra() {
      super(new CommentLoading(getContext()), Position.END);
      commentLoading = (CommentLoading) view;
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
      commentLoading.start();
      commentLoading.setVisibility(View.VISIBLE);
    }

    private void finishLoading() {
      commentLoading.cancel();
      commentLoading.setVisibility(View.GONE);
    }
  }


  private class PostItemExtra extends RecyclerViewExtra {
    private HandlerAccess handlerAccess;
    private PostItemView postItemView;

    public PostItemExtra(HandlerAccess handlerAccess) {
      super(new PostItemView(backButton.getContext()), Position.START);
      this.handlerAccess = handlerAccess;
      FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.WRAP_CONTENT);
      params.bottomMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
              20,
              getContext().getResources().getDisplayMetrics());
      this.view.setLayoutParams(params);
    }

    @Override
    public void configure(View view) {
      postItemView = (PostItemView) view;
      postItemView.initViewModel(handlerAccess);
    }
  }

  public static PostDetailsFragment newInstance(HandlerAccess handlerAccess, String commendId) {
    Bundle args = new Bundle();
    args.putString("comment id", commendId);
    args.putString("post id", handlerAccess.getItemId());
    args.putInt("access id", handlerAccess.getId());
    PostDetailsFragment fragment = new PostDetailsFragment(handlerAccess);
    fragment.setArguments(args);
    return fragment;
  }

  public PostDetailsFragment() {
  }

  public PostDetailsFragment(HandlerAccess handlerAccess) {
    this.handlerAccess = handlerAccess;
  }

  private HandlerAccess handlerAccess;
  private String commentId, postId;
  private LiveData<HandlerAccess> sessionAccessLiveData;
  private ActivityResultLauncher<String> pickImage;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    commentId = getArguments().getString("comment id");
    postId = getArguments().getString("post id");

    if (handlerAccess != null) {
      sessionAccessLiveData = new MutableLiveData<>(handlerAccess);
    } else {
      Integer accessId = getArguments().getInt("access id");
      sessionAccessLiveData = PostHandlerStore
              .getInstance()
              .findHandlerAccess(postId, accessId);
    }
    viewModel = new ViewModelProvider(this
            , new ViewModelFactory(this, null))
            .get(PostDetailsFragmentViewModel.class);
    pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
      @Override
      public void onActivityResult(Uri uri) {
        if (uri == null) return;
        viewModel.getImage().setValue(uri);
      }
    });
  }

  private PostDetailsFragmentViewModel viewModel;
  private ExtraViewHolder topViews;
  private RecyclerView recyclerView;
  private CustomSpinningView spinnerLoading;
  private CommentAdapter adapter;
  private EditText commentEditText;
  private CircleButton selectImage, selectIcon, selectGif, sendButton;
  private CircleButton eraseImageButton;
  private View imageContainer;
  private ImageView imageView;
  private EditTextActionHelper actionHelper;
  private Function<Bundle, LiveData<String>> defaultAction;
  private View backButton;
  private View root;
  private UltimateRoundedButton hintButton;
  private LinearLayoutManager layoutManager;
  private CommentGroupManager commentManager;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_post_details, container, false);
    root = view;
    spinnerLoading = view.findViewById(R.id.load_spinner);
    recyclerView = view.findViewById(R.id.comment_panel);
    selectImage = view.findViewById(R.id.select_image);
    selectIcon = view.findViewById(R.id.select_icon);
    selectGif = view.findViewById(R.id.select_gif);
    sendButton = view.findViewById(R.id.send_button);
    imageView = view.findViewById(R.id.image_view);
    imageContainer = view.findViewById(R.id.image_container);
    eraseImageButton = view.findViewById(R.id.remove_media_button);
    commentEditText = view.findViewById(R.id.comment_edit_text);
    backButton = view.findViewById(R.id.back_button);
    hintButton = view.findViewById(R.id.new_comment_button);

    spinnerLoading.setVisibility(View.VISIBLE);


    root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        performStart();
      }
    });

    actionHelper = new EditTextActionHelper(commentEditText);
    sessionAccessLiveData.observe(getViewLifecycleOwner(), new Observer<HandlerAccess>() {
      @Override
      public void onChanged(HandlerAccess handlerAccess) {
        PostDetailsFragment.this.handlerAccess = handlerAccess;
        initViewModel();
      }
    });

    sessionAccessLiveData.observe(getViewLifecycleOwner(), sessionHandler -> {
    });
    return view;
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  private void postConstruct(CommentRepository repo, int index) {
    viewModel.setRepo(repo);
    initRecyclerView(repo);
    LiveData<Boolean> newComment = viewModel.getHintComment();
    newComment.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
      @Override
      public void onChanged(Boolean aBoolean) {
        hintButton.setVisibility(View.VISIBLE);
        hintButton.setAlpha(0);
        hintButton.animate()
                .alpha(1)
                .setDuration(100)
                .start();
      }
    });
    viewModel.getLoadState().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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
    commentEditText.postDelayed(() -> {
      int pos = index + topViews.size();
      recyclerView.smoothScrollToPosition(pos);
    }, 200);
  }

  private void initRecyclerView(CommentRepository commentRepo) {
    topViews = new ExtraViewHolder(RecyclerViewExtra.Position.START, new ArrayList<>());
    topViews.add(new PostItemExtra(handlerAccess));
    topViews.add(new SendingCommentViewExtra());
    List<RecyclerViewExtra> endViews = new ArrayList<>();
    endViews.add(new CommentLoadingExtra());
    commentManager = new CommentGroupManager(
            getContext(),
            getViewLifecycleOwner(),
            commentRepo,
            actionHelper);
    adapter = new CommentAdapter(commentManager);
    adapter.applyExtraViews(topViews, new ExtraViewHolder(RecyclerViewExtra.Position.END, new ArrayList<>()));
    commentManager.setAdapter(adapter);
    recyclerView.setAdapter(adapter);

    layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int pos = layoutManager.findLastVisibleItemPosition();
        int total = adapter.getItemCount();

        if (pos + 2 >= total) {
          viewModel.load(2);
        }
      }
    });
    loadPreloadComments(commentRepo);
  }

  private void loadPreloadComments(Repository<HandlerAccess> commentRepo) {
    List<CommentGroup> commentGroups = new ArrayList<>();
    for (int i = 0; i < commentRepo.length(); i++) {
      CommentGroup commentGroup = new CommentGroup(
              this,
              commentRepo.get(i),
              actionHelper
      );
      commentGroups.add(commentGroup);
    }
    commentManager.insert(commentGroups, true);
  }

  public void initViewModel() {
    PostSessionHandler postSessionHandler = handlerAccess.access();
    CommentRepository repo = new CommentRepository(postSessionHandler.getCommentAccessHandler());
    LiveData<Integer> indexLiveData = repo.loadUpTo(sessionAccess -> {
      CommentSessionHandler right = sessionAccess.access();
      String id = right.getCommentData().getValue().getId();
      return commentId.compareTo(id);
    });
    indexLiveData.observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer index) {
        spinnerLoading.setVisibility(View.GONE);
        if (index == null) {
          return;
        }
        postConstruct(repo, index);
      }
    });
    initSendAction(repo);
    viewModel.getContent().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        if (!Objects.equals(s, commentEditText.getText().toString())) {
          commentEditText.setText(s);
        }
        viewModel.getContent().removeObserver(this);
      }
    });
    viewModel.getContent().observe(getViewLifecycleOwner(), new Observer<String>() {
      @Override
      public void onChanged(String s) {
        if (s.isEmpty()) {
          actionHelper.setActionOnEditText(null, defaultAction);
        }
      }
    });
    commentEditText.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

      }

      @Override
      public void afterTextChanged(Editable editable) {
        viewModel.getContent().setValue(editable.toString());
      }
    });
    viewModel.getCntEditedContent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer == 0) {
          sendButton.setBackgroundContent(
                  getResources().getDrawable(R.drawable.comment_send, null),
                  -1);
          sendButton.setClickedEnable(false);
        } else {
          sendButton.setBackgroundContent(
                  getResources().getDrawable(R.drawable.comment_active_send, null),
                  -1);
          sendButton.setClickedEnable(true);
        }
      }
    });
    viewModel.getImage().observe(getViewLifecycleOwner(), new Observer<Uri>() {
      @Override
      public void onChanged(Uri uri) {
        if (uri == null) {
          imageContainer.setVisibility(View.GONE);
        } else {
          imageContainer.setVisibility(View.VISIBLE);
          imageView.setImageURI(uri);
        }
      }
    });
    initOnClick();
  }

  private void initSendAction(CommentRepository repo) {
    UploadAdapter<HandlerAccess> uploadAdapter = new UploadAdapter<>(CommentUploadTask.class);
    repo.setUploadAdapter(uploadAdapter);
    defaultAction = data -> {
      MutableLiveData<String> sendCommentState = viewModel.getSendState();
      sendCommentState.setValue("Sending");
      LiveData<String> callBack = uploadAdapter.uploadNewItem(data);
      callBack.observe(getViewLifecycleOwner(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          if (!s.equals("Success")) {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
          }
          sendCommentState.setValue("Idle");
        }
      });
      return callBack;
    };
    actionHelper.setActionOnEditText(null, defaultAction);
  }

  private void initOnClick() {
    backButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        getActivity().onBackPressed();
      }
    });
    selectImage.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        pickImage.launch("image/*");
      }
    });
    eraseImageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        viewModel.getImage().setValue(null);
      }
    });
    viewModel.getCntEditedContent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        if (integer == 0) {
          sendButton.setOnClickListener(null);
          return;
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            MutableLiveData<Uri> imageUri = viewModel.getImage();
            Uri uri = imageUri.getValue();
            MutableLiveData<String> content = viewModel.getContent();

            Bundle data = new Bundle();
            data.putString("content", content.getValue());
            data.putString("image content", uri == null ? null : uri.toString());

            LiveData<String> actionCallBack = actionHelper.doAction(data);

            imageUri.setValue(null);
            commentEditText.setText("");
            if (actionCallBack == null) {
              Toast.makeText(getContext(), "You can't do this action right now", Toast.LENGTH_SHORT).show();
              return;
            }
            actionCallBack.observe(getViewLifecycleOwner(), new Observer<String>() {
              @Override
              public void onChanged(String s) {
              }
            });
          }
        });
      }
    });

  }

  @Override
  public void performEnd(Runnable endAction) {
    root.animate()
            .translationX(root.getWidth())
            .setDuration(200)
            .withEndAction(() -> endAction.run())
            .start();
  }

  @Override
  public void performStart() {
    root.setTranslationX(((View) root.getParent()).getWidth() * 66 / 100);
    root.animate()
            .translationX(0)
            .setDuration(200)
            .start();
  }
}
