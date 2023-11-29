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
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.DragPanel;
import com.example.socialmediaapp.customview.progress.SendCommentloading;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.CommentFragment;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.MainCommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.PostDataViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.post.Comment;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.Objects;

public class MainCommentFragment extends Fragment implements FragmentAnimation {

    public MainCommentFragment() {
    }

    public MainCommentFragment(PostDataViewModel postDataViewModel) {
        this.postDataViewModel = postDataViewModel;
    }

    public static MainCommentFragment newInstance(Bundle args, PostDataViewModel postDataViewModel) {
        MainCommentFragment fragment = new MainCommentFragment(postDataViewModel);
        fragment.setArguments(args);
        return fragment;
    }

    private PostDataViewModel postDataViewModel;
    private MainCommentFragmentViewModel viewModel;
    private ActivityResultLauncher<String> pickImage;

    public MainCommentFragmentViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        assert args != null;

        Integer sessionId = args.getInt("session id");

        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(MainCommentFragmentViewModel.class);
        viewModel.setSessionId(sessionId);
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
    private CircleButton selectImage, selectIcon, selectGif, sendButton;
    private View root;
    private CircleButton eraseImageButton;
    private View imageContainer;
    private ImageView imageView;
    private ViewGroup mainPanel;
    private View commandPanel;
    private View padding;
    private DragPanel dragPanel;
    private MutableLiveData<String> sessionState;
    private HomePage homePage;
    private SendCommentloading sendCommentloading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

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
        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        dragPanel.setFinishAction(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        viewModel.getCommentFragmentSession().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.comment_fragment_container, new CommentFragment((DataAccessHandler<Comment>) sessionHandler), "comments");
                fragmentTransaction.commit();

                initOnclick();
            }
        });
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

        initViewModel();
        return root;
    }

    private void initViewModel() {
        if (postDataViewModel != null) {
            postDataViewModel.getCountLike().observe(getViewLifecycleOwner(), integer -> viewModel.getCountLike().setValue(integer));
        }
        viewModel.getCountLike().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                cntLikeTextView.setText(Integer.toString(integer));
            }
        });
        viewModel.getCommentContent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, commentEditText.getText().toString())) {
                    commentEditText.setText(s);
                }
                viewModel.getCommentContent().removeObserver(this);
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
                viewModel.getCommentContent().setValue(editable.toString());
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
        viewModel.getSendState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Sending")) {
                    performSending();
                } else if (s.equals("Complete")) {
                    finishSending();
                    viewModel.getSendState().setValue("Idle");
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
                Uri uri = viewModel.getImage().getValue();

                Bundle data = new Bundle();
                data.putString("content", viewModel.getCommentContent().getValue());
                data.putString("image", uri == null ? null : uri.toString());

                CommentFragment commentFragment = (CommentFragment) getChildFragmentManager().findFragmentByTag("comments");
                CommentFragmentViewModel commentFragmentViewModel = commentFragment.getViewModel();

                LiveData<String> callBack = commentFragmentViewModel.uploadComment(data);

                MediatorLiveData<String> sendState = viewModel.getSendState();
                sendState.setValue("Sending");
                sendState.addSource(callBack, new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (!s.equals("Success")) {
                            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                        }
                        sendState.setValue("Complete");
                        sendState.removeSource(callBack);
                    }
                });

            }
        });
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

}
