package com.example.socialmediaapp.home.fragment.comment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activity.HomePage;
import com.example.socialmediaapp.application.repo.core.CommentRepository;
import com.example.socialmediaapp.application.repo.core.UploadAdapter;
import com.example.socialmediaapp.application.session.CommentHandlerStore;
import com.example.socialmediaapp.application.session.CommentUploadTask;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.PostSessionHandler;
import com.example.socialmediaapp.application.session.HandlerAccess;
import com.example.socialmediaapp.home.fragment.extras.EditTextActionHelper;
import com.example.socialmediaapp.view.button.CircleButton;
import com.example.socialmediaapp.view.container.DragPanel;
import com.example.socialmediaapp.view.progress.SendCommentloading;
import com.example.socialmediaapp.home.fragment.extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.fragment.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.fragment.MainCommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MainCommentFragment extends Fragment implements FragmentAnimation {

  public class SendingCommentViewExtra extends RecyclerViewExtra {
    private SendCommentloading sendloading;

    public SendingCommentViewExtra() {
      super(new SendCommentloading(getContext()), Position.START);
      sendloading = (SendCommentloading) view;
    }

    @Override
    public void configure(View view) {

      UserSessionViewModel hostViewModel = homePage.getViewModel();
      hostViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
        @Override
        public void onChanged(Bitmap bitmap) {
          Drawable avatar = bitmap == null ? null : new BitmapDrawable(getResources(), bitmap);
          sendloading.setAvatar(avatar);
        }
      });
      hostViewModel.getFullname().observe(getViewLifecycleOwner(), new Observer<String>() {
        @Override
        public void onChanged(String s) {
          sendloading.setFullname(s);
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
    }

    private void performSending() {
      LiveData<Uri> imageUri = viewModel.getImage();
      LiveData<String> content = viewModel.getContent();
      sendloading.setLoadingContent(content.getValue(), imageUri.getValue());
      sendloading.setVisibility(View.VISIBLE);
    }

    private void finishSending() {
      sendloading.setVisibility(View.GONE);
    }
  }

  public class SendActionConfigurator implements CommentFragment.ConfigureExtra {
    @Override
    public void apply(View root, CommentFragmentViewModel fragmentViewModel) {
      CommentRepository repo = fragmentViewModel.getRepo();
      UploadAdapter<HandlerAccess> uploadAdapter = new UploadAdapter<>(CommentUploadTask.class);
      repo.setUploadAdapter(uploadAdapter);

      defaultAction = data -> {
        MediatorLiveData<String> sendCommentState = viewModel.getSendState();
        sendCommentState.setValue("Sending");
        LiveData<String> callBack = uploadAdapter.uploadNewItem(data);
        callBack.observe(getViewLifecycleOwner(), new Observer<String>() {
          @Override
          public void onChanged(String s) {
            if (!s.equals("Success")) {
              Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }
            sendCommentState.setValue("Complete");
          }
        });
        return callBack;
      };
      actionHelper.setActionOnEditText(null, defaultAction);
    }
  }

  public class DragToCloseConfigurator implements CommentFragment.ConfigureExtra {
    @Override
    public void apply(View root, CommentFragmentViewModel commentFragmentViewModel) {
      RecyclerView recyclerView = root.findViewById(R.id.comment_panel);
      dragPanel.setDragHelper(new DragPanel.RecyclerViewDragHelper(recyclerView));
    }
  }

  public MainCommentFragment() {
  }

  public MainCommentFragment(HandlerAccess sessionClient) {
    this.sessionAccessLiveData = new MutableLiveData<>(sessionClient);
  }

  public static MainCommentFragment newInstance(HandlerAccess access) {
    MainCommentFragment fragment = new MainCommentFragment(access);
    Bundle args = new Bundle();
    args.putInt("access id", access.getId());
    args.putString("comment id", access.getItemId());
    fragment.handlerAccess = access;
    fragment.setArguments(args);
    return fragment;
  }

  private HandlerAccess handlerAccess;
  private LiveData<HandlerAccess> sessionAccessLiveData;
  private MainCommentFragmentViewModel viewModel;
  private ActivityResultLauncher<String> pickImage;

  public MainCommentFragmentViewModel getViewModel() {
    return viewModel;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (handlerAccess != null) {
      sessionAccessLiveData = new MutableLiveData<>(handlerAccess);
    } else {
      Integer accessId = getArguments().getInt("access id");
      String commentId = getArguments().getString("comment id");

      sessionAccessLiveData = CommentHandlerStore
              .getInstance()
              .findHandlerAccess(commentId, accessId);
    }
    pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
      @Override
      public void onActivityResult(Uri uri) {
        if (uri == null) return;
        viewModel.getImage().setValue(uri);
      }
    });
  }

  private TextView cntLikeTextView;
  private EditText commentEditText;
  private View root;
  private CircleButton selectImage, selectIcon, selectGif, sendButton;
  private CircleButton eraseImageButton;
  private View imageContainer;
  private ImageView imageView;
  private DragPanel dragPanel;
  private LiveData<String> sessionState;
  private HomePage homePage;
  private EditTextActionHelper actionHelper;
  private Function<Bundle, LiveData<String>> defaultAction;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    root = inflater.inflate(R.layout.fragment_main_comment, container, false);
    cntLikeTextView = root.findViewById(R.id.cnt_like);
    selectImage = root.findViewById(R.id.select_image);
    selectIcon = root.findViewById(R.id.select_icon);
    selectGif = root.findViewById(R.id.select_gif);
    sendButton = root.findViewById(R.id.send_button);
    imageView = root.findViewById(R.id.image_view);
    imageContainer = root.findViewById(R.id.image_container);
    eraseImageButton = root.findViewById(R.id.remove_media_button);
    commentEditText = root.findViewById(R.id.comment_edit_text);
    dragPanel = root.findViewById(R.id.drag_panel);

    actionHelper = new EditTextActionHelper(commentEditText);
    homePage = (HomePage) getActivity();

    dragPanel.setDragListener(new DragPanel.DragAdapter() {
      @Override
      public void onFinish() {
        getActivity().getSupportFragmentManager().popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
      }
    });
    LiveData<DataAccessHandler<HandlerAccess>> commentAccessHandlerLiveData;
    commentAccessHandlerLiveData = Transformations.map(sessionAccessLiveData, new androidx.arch.core.util.Function<HandlerAccess, DataAccessHandler<HandlerAccess>>() {
      @Override
      public DataAccessHandler<HandlerAccess> apply(HandlerAccess input) {
        return ((PostSessionHandler) input.access()).getCommentAccessHandler();
      }
    });

    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
    RecyclerViewExtra sendCommentView = new SendingCommentViewExtra();

    List<CommentFragment.ConfigureExtra> configs = new ArrayList<>();
    configs.add(new CommentFragment.ScrollConfigurator());
    configs.add(new SendActionConfigurator());
    configs.add(new DragToCloseConfigurator());

    List<RecyclerViewExtra> extras = new ArrayList<>();
    extras.add(sendCommentView);

    CommentFragment commentFragment = new CommentFragment(commentAccessHandlerLiveData
            , configs
            , extras);
    fragmentTransaction.replace(R.id.comment_fragment_container, commentFragment, "comments");
    fragmentTransaction.commit();
    sessionAccessLiveData.observe(getViewLifecycleOwner(), new Observer<HandlerAccess>() {
      @Override
      public void onChanged(HandlerAccess sessionClient) {
        initViewModel(sessionClient.access());
      }
    });

    return root;
  }

  @Override
  public void onStart() {
    super.onStart();
    performStart();
  }

  private void initViewModel(PostSessionHandler postHandler) {
    viewModel = new ViewModelProvider(this
            , new ViewModelFactory(this, null))
            .get(MainCommentFragmentViewModel.class);
    viewModel.initCountLikeContent(postHandler.getPostData(), getViewLifecycleOwner());
    viewModel.getCountLikeContent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
      @Override
      public void onChanged(Integer integer) {
        cntLikeTextView.setText(Integer.toString(integer));
      }
    });
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
          sendButton.setBackgroundContent(getResources().getDrawable(R.drawable.comment_send, null), -1);
          sendButton.setClickedEnable(false);
        } else {
          sendButton.setBackgroundContent(getResources().getDrawable(R.drawable.comment_active_send, null), -1);
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
    initOnclick();
  }

  private void initOnclick() {
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
            MutableLiveData<String> sendState = viewModel.getSendState();
            boolean isSending = sendState.equals("Sending");
            if (isSending) {
              Toast.makeText(getContext(), "please wait", Toast.LENGTH_SHORT).show();
              return;
            }

            MutableLiveData<Uri> imageUri = viewModel.getImage();
            Uri uri = imageUri.getValue();
            MutableLiveData<String> content = viewModel.getContent();

            Bundle data = new Bundle();
            data.putString("content", content.getValue());
            data.putString("image content", uri == null ? null : uri.toString());

            LiveData<String> actionCallBack = actionHelper.doAction(data);
            if (actionCallBack == null) {
              Toast.makeText(homePage, "You can't do this action right now", Toast.LENGTH_SHORT).show();
              return;
            }
            imageUri.setValue(null);
            commentEditText.setText("");
            actionCallBack.observe(getViewLifecycleOwner(), new Observer<String>() {
              @Override
              public void onChanged(String s) {
                sendState.setValue("Idle");
              }
            });
          }
        });

      }
    });
  }

  public EditTextActionHelper getActionHelper() {
    return actionHelper;
  }

  @Override
  public void performEnd(Runnable endAction) {
    root.animate()
            .translationY(root.getHeight())
            .setDuration(150)
            .withEndAction(new Runnable() {
              @Override
              public void run() {
                endAction.run();
              }
            }).start();
  }

  @Override
  public void performStart() {
    commentEditText.requestFocus();
    View p = (View) getView().getParent();

    root.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    root.setTranslationY(p.getHeight() * 3 / 5);
    root.animate()
            .translationY(0)
            .setDuration(150)
            .setInterpolator(new DecelerateInterpolator())
            .withEndAction(new Runnable() {
              @Override
              public void run() {
                root.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
              }
            });
  }

}
