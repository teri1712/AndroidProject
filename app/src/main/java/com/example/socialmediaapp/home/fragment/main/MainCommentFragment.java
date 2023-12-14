package com.example.socialmediaapp.home.fragment.main;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.DragPanel;
import com.example.socialmediaapp.customview.progress.SendCommentloading;
import com.example.socialmediaapp.home.fragment.Extras.RecyclerViewExtra;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.MainCommentFragmentViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class MainCommentFragment extends Fragment implements FragmentAnimation {

    public class SendingCommentViewExtra extends RecyclerViewExtra {
        private SendCommentloading sendCommentloading;

        public SendingCommentViewExtra(View view) {
            super(view, Position.START);
            sendCommentloading = (SendCommentloading) view;
        }

        @Override
        public void configure(View view) {

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

            viewModel.getSendCommentState().observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if (s.equals("Idle")) {
                        finishSending();
                    } else if (s.equals("Sending")) {
                        performSending();
                    } else {
                        viewModel.getSendCommentState().setValue("Idle");
                    }
                }
            });

            LiveData<Uri> imageUri = viewModel.getImage();
            LiveData<String> content = viewModel.getCommentContent();
            LiveData<String> sending = viewModel.getSendCommentState();
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

    public class SendActionConfigurator implements CommentFragment.ConfigureExtra {
        @Override
        public void apply(View root, CommentFragmentViewModel commentFragmentViewModel) {
            defaultAction = data -> {
                MediatorLiveData<String> sendCommentState = viewModel.getSendCommentState();
                sendCommentState.setValue("Sending");
                LiveData<String> callBack = commentFragmentViewModel.uploadComment(data);
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
            actionOnEditText = defaultAction;
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

    public MainCommentFragment(LiveData<Integer> postHostCountLike) {
        this.postHostCountLike = postHostCountLike;
    }

    public static MainCommentFragment newInstance(Bundle args, LiveData<Integer> postHostCountLike) {
        MainCommentFragment fragment = new MainCommentFragment(postHostCountLike);
        fragment.setArguments(args);
        return fragment;
    }

    private LiveData<Integer> postHostCountLike;
    private MainCommentFragmentViewModel viewModel;
    private ActivityResultLauncher<String> pickImage;
    private Integer sessionId;

    public MainCommentFragmentViewModel getViewModel() {
        return viewModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        this.sessionId = args.getInt("session id");

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
    private DragPanel dragPanel;
    private LiveData<String> sessionState;
    private HomePage homePage;
    private Function<Bundle, LiveData<String>> actionOnEditText;
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


        homePage = (HomePage) getActivity();

        dragPanel.setDragListener(new DragPanel.DragAdapter() {
            @Override
            public void onFinish() {
                getActivity().getSupportFragmentManager().popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        initViewModel();

        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        RecyclerViewExtra sendCommentView = new SendingCommentViewExtra(new SendCommentloading(getContext()));

        List<CommentFragment.ConfigureExtra> configureExtras = new ArrayList<>();
        configureExtras.add(new CommentFragment.ScrollConfigurator());
        configureExtras.add(new SendActionConfigurator());
        configureExtras.add(new DragToCloseConfigurator());

        List<RecyclerViewExtra> extras = new ArrayList<>();
        extras.add(sendCommentView);

        CommentFragment commentFragment = new CommentFragment(viewModel.getCommentFragmentSession(), configureExtras, extras);
        fragmentTransaction.replace(R.id.comment_fragment_container, commentFragment, "comments");
        fragmentTransaction.commit();

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        performStart();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(MainCommentFragmentViewModel.class);
        viewModel.setSessionId(sessionId);

        if (postHostCountLike != null) {
            postHostCountLike.observe(getViewLifecycleOwner(), integer -> viewModel.getCountLike().setValue(integer));
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

        viewModel.getCommentFragmentSession().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                sessionHandler.setRetain(true);
                initOnclick();
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
                if (viewModel.isSending()) {
                    Toast.makeText(getContext(), "please wait", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.setSending(true);

                MutableLiveData<Uri> imageUri = viewModel.getImage();
                Uri uri = imageUri.getValue();
                MutableLiveData<String> content = viewModel.getCommentContent();

                Bundle data = new Bundle();
                data.putString("content", content.getValue());
                data.putString("image content", uri == null ? null : uri.toString());

                actionOnEditText.apply(data).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        viewModel.setSending(false);
                    }
                });

                imageUri.setValue(null);
                commentEditText.setText("");
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
                boolean isEmpty = editable.length() == 0;
                if (isEmpty) {
                    actionOnEditText = defaultAction;
                }
            }
        });
    }

    public void setActionOnEditText(String name, Function<Bundle, LiveData<String>> action) {
        actionOnEditText = action;

        SpannableString spannableString = new SpannableString(name);

        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.parseColor("#3F0866FF"));
        spannableString.setSpan(backgroundColorSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        commentEditText.setText(spannableString);
        commentEditText.setSelection(commentEditText.getText().length());

        commentEditText.requestFocus();
    }

    @Override
    public void performEnd(Runnable endAction) {
        ApplicationContainer.getInstance().sessionRepository.deleteIfDetached(sessionId);
        root.animate().translationY(root.getHeight()).setDuration(150).withEndAction(new Runnable() {
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
        root.setTranslationY(p.getHeight() / 2);
        root.animate().translationY(0).setDuration(200).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                root.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }
        });
    }

}
