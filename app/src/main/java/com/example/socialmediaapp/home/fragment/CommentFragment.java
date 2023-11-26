package com.example.socialmediaapp.home.fragment;

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
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.container.ApplicationContainer;
import com.example.socialmediaapp.container.session.CommentSessionHandler;
import com.example.socialmediaapp.container.session.DataAccessHandler;
import com.example.socialmediaapp.container.session.OnlineSessionHandler;
import com.example.socialmediaapp.container.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.DragPanel;
import com.example.socialmediaapp.customview.progress.CommentLoading;
import com.example.socialmediaapp.customview.progress.SendCommentloading;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.layoutviews.items.CommentItemView;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.repo.Repository;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.refactor.CommentDataViewModel;
import com.example.socialmediaapp.viewmodel.refactor.UserSessionViewModel;

import java.util.List;
import java.util.Objects;

public class CommentFragment extends Fragment implements FragmentAnimation {

    public CommentFragment() {
    }

    static public CommentFragment newInstance(Bundle args) {
        CommentFragment commentFragment = new CommentFragment();
        commentFragment.setArguments(args);
        return commentFragment;
    }

    private Integer countLike;
    private CommentFragmentViewModel viewModel;
    private ActivityResultLauncher<String> pickImage;
    private MutableLiveData<SessionHandler> sessionHandlerMutableLiveData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;

        countLike = args.getInt("count like");
        Integer sessionId = args.getInt("session id");

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(CommentFragmentViewModel.class);
        OnlineSessionHandler onlineSessionHandler = ApplicationContainer.getInstance().onlineSessionHandler;

        sessionHandlerMutableLiveData = onlineSessionHandler.getSessionById(sessionId);


        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri == null) return;
                viewModel.getImage().setValue(uri);
            }
        });
    }

    private TextView cntLikeTextView;
    private ViewGroup commentPanel;
    private EditText commentEditText;
    private CircleButton selectImage, selectIcon, selectGif, sendButton;
    private View root;
    private CircleButton eraseImageButton;
    private View imageContainer;
    private ImageView imageView;
    private CommentLoading commentLoadingAnimator;
    private SendCommentloading sendCommentloading;
    private ViewGroup mainPanel;
    private View commandPanel;
    private View padding;
    private DragPanel dragPanel;
    private SessionHandler.SessionRegistry sessionRegistry;
    private MutableLiveData<String> sessionState;
    private Repository<Comment> commentRepository;

    private HomePage homePage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_comment, container, false);
        cntLikeTextView = root.findViewById(R.id.cnt_like);
        commentPanel = root.findViewById(R.id.comment_panel);
        selectImage = root.findViewById(R.id.select_image);
        selectIcon = root.findViewById(R.id.select_icon);
        selectGif = root.findViewById(R.id.select_gif);
        sendButton = root.findViewById(R.id.send_button);
        imageView = root.findViewById(R.id.image_view);
        imageContainer = root.findViewById(R.id.image_container);
        eraseImageButton = root.findViewById(R.id.remove_media_button);
        commentEditText = root.findViewById(R.id.comment_edit_text);
        mainPanel = root.findViewById(R.id.main_panel);
        padding = root.findViewById(R.id.padding);
        commandPanel = root.findViewById(R.id.command_panel);
        dragPanel = root.findViewById(R.id.drag_panel);

        homePage = (HomePage) getActivity();
        commandPanel.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                padding.getLayoutParams().height = commandPanel.getHeight();
                padding.requestLayout();
            }
        });

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        initViewModel();

        sessionHandlerMutableLiveData.observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                viewModel.setDataAccessHandler((DataAccessHandler<Comment>) sessionHandler);

                commentRepository = viewModel.getCommentRepository();
                sessionRegistry = viewModel.getSessionRegistry();
                sessionState = viewModel.getSessionState();
                sessionState.observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s.equals("started")) {
                            sessionHandler.setActive();
                            Bundle query = new Bundle();
                            performLoading();
                            commentRepository.fetchNewItems(query).observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
                                @Override
                                public void onChanged(List<Comment> comments) {
                                    showComments(comments);
                                    finishLoading();
                                }
                            });
                            initOnclick();
                        }
                    }
                });
            }
        });

        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        dragPanel.setFinishAction(new Runnable() {
            @Override
            public void run() {
                viewModel.getSessionHandler().setInActive();
                getActivity().getSupportFragmentManager().popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        commentLoadingAnimator = new CommentLoading(getContext());
        sendCommentloading = new SendCommentloading(getContext());

        UserSessionViewModel userSessionViewModel = homePage.getViewModel();

        userSessionViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                Drawable avatar = bitmap == null ? null : new BitmapDrawable(getResources(), bitmap);
                sendCommentloading.setAvatar(avatar);
            }
        });
        userSessionViewModel.getUserInfo().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userInformation) {
                String fn = userInformation.getFullname();
                sendCommentloading.setFullname(fn);
            }
        });

        return root;
    }

    private void initViewModel() {
        cntLikeTextView.setText(Integer.toString(countLike));
        commentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getCommentContent().setValue(editable.toString());
            }
        });
        viewModel.getCommentContent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, commentEditText.getText().toString())) {
                    commentEditText.setText(s);
                }
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
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.getCntEditedContent().getValue() == 0) {
                    return;
                }
                performSending();
                Bundle data = new Bundle();
                data.putString("content", viewModel.getCommentContent().getValue());
                Uri uri = viewModel.getImage().getValue();
                data.putString("image", uri == null ? null : uri.toString());

                commentRepository.uploadNewItem(data).observe(getViewLifecycleOwner(), new Observer<Integer>() {
                    @Override
                    public void onChanged(Integer integer) {
                        Bundle query = new Bundle();
                        commentRepository.fetchNewItems(query).observe(getViewLifecycleOwner(), new Observer<List<Comment>>() {
                            @Override
                            public void onChanged(List<Comment> comments) {
                                showComments(comments);
                                finishSending();
                            }
                        });
                    }
                });
                viewModel.getCommentContent().setValue("");
                viewModel.getImage().setValue(null);
            }
        });
    }

    private void showComments(List<Comment> comments) {
        for (Comment comment : comments) {
            CommentSessionHandler commentSessionHandler = new CommentSessionHandler(comment.getId());
            sessionRegistry.register(commentSessionHandler);
            CommentDataViewModel commentDataViewModel = new CommentDataViewModel(commentSessionHandler, comment);
            CommentItemView commentItemView = new CommentItemView(CommentFragment.this, commentDataViewModel);
        }
    }

    @Override
    public void performEnd(Runnable endAction) {
        root.animate().translationY(root.getHeight()).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {
                endAction.run();
            }
        }).start();
    }

    @Override
    public void performStart() {
        View p = (View) getView().getParent();

        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        root.setTranslationY(p.getHeight() * 45 / 100);
        root.animate().translationY(0).setDuration(200).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                root.setLayerType(View.LAYER_TYPE_NONE, null);
                commentEditText.requestFocus();
            }
        });
    }

    private void performLoading() {
        commentPanel.addView(commentLoadingAnimator);
        commentLoadingAnimator.start();
    }

    private void finishLoading() {
        commentLoadingAnimator.cancel();
        commentPanel.removeView(commentLoadingAnimator);
    }

    private void performSending() {
        String c = viewModel.getCommentContent().getValue();
        Uri image = viewModel.getImage().getValue();
        sendCommentloading.setLoadingContent(c, image);
        mainPanel.addView(sendCommentloading, 0);
    }

    private void finishSending() {
        mainPanel.removeView(sendCommentloading);
    }

}
