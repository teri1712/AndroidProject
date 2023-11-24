package com.example.socialmediaapp.home.fragment;

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

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.DragPanel;
import com.example.socialmediaapp.customview.progress.CommentLoading;
import com.example.socialmediaapp.customview.progress.SendCommentloading;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.layoutviews.items.CommentItemView;
import com.example.socialmediaapp.viewmodels.CommentFragmentViewModel;
import com.example.socialmediaapp.viewmodels.items.PostItemViewModel;
import com.example.socialmediaapp.viewmodels.models.post.Comment;
import com.example.socialmediaapp.viewmodels.models.repo.Update;

import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentFragment extends Fragment implements FragmentAnimation {


    public CommentFragment(PostItemViewModel postViewModel) {
        this.postViewModel = postViewModel;
    }

    private ActivityResultLauncher<String> pickImage;

    public static CommentFragment newInstance(PostItemViewModel post) {
        CommentFragment fragment = new CommentFragment(post);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri == null) return;
                viewModel.getImage().setValue(uri);
            }
        });
        viewModel = new CommentFragmentViewModel();
    }

    private TextView cntLikeTextView;
    private ViewGroup commentPanel;
    private EditText commentEditText;
    private CircleButton selectImage, selectIcon, selectGif, sendButton;
    private View root;
    private CommentFragmentViewModel viewModel;
    private PostItemViewModel postViewModel;
    private CircleButton eraseImageButton;
    private View imageContainer;
    private ImageView imageView;
    private CommentLoading commentLoadingAnimator;
    private SendCommentloading sendCommentloading;
    private ViewGroup mainPanel;
    private View commandPanel;
    private View padding;

    private DragPanel dragPanel;

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
        initOnclick();
        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        dragPanel.setFinishAction(new Runnable() {
            @Override
            public void run() {
                getActivity().getSupportFragmentManager().popBackStackImmediate(getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        commentLoadingAnimator = new CommentLoading(getContext());
        sendCommentloading = new SendCommentloading(getContext());
        return root;
    }

    private void initViewModel() {
        postViewModel.getCountLikeContent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                cntLikeTextView.setText(s);
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
        postViewModel.getCommentRepo().getUpdateOnRepo().observe(getViewLifecycleOwner(), new Observer<Update<Comment>>() {
            @Override
            public void onChanged(Update<Comment> commentUpdate) {
                if (commentUpdate == null) return;
                int pos = commentUpdate.getPos();
                if (commentUpdate.getOp() == Update.Op.ADD) {
                    commentPanel.addView(new CommentItemView(CommentFragment.this, postViewModel.getCommentRepo(), pos), pos);
                } else {
                    commentPanel.removeViewAt(pos);
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
                postViewModel.sendComment(getContext(), viewModel.getCommentContent().getValue(), viewModel.getImage().getValue()).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s.equals("Success")) {
                        }
                        finishSending();
                    }
                });
                viewModel.getCommentContent().setValue("");
                viewModel.getImage().setValue(null);
            }
        });

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
        List<Comment> l = postViewModel.getCommentRepo().findAllItem();
        if (l.isEmpty()) {
            performLoading();
            postViewModel.loadComments(getContext()).observe(getViewLifecycleOwner(), new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    finishLoading();
                }
            });
        } else {
            for (int pos = 0; pos < l.size(); pos++) {
                commentPanel.addView(new CommentItemView(CommentFragment.this, postViewModel.getCommentRepo(), pos));
            }
        }
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
        String fn = postViewModel.getPost().getAuthor().getFullname();
        Drawable avatar = postViewModel.getPost().getAuthor().getAvatar();
        String c = viewModel.getCommentContent().getValue();
        Uri image = viewModel.getImage().getValue();
        sendCommentloading.setLoadingContent(fn, avatar, c, image);
        mainPanel.addView(sendCommentloading, 0);
    }

    private void finishSending() {
        mainPanel.removeView(sendCommentloading);
    }

}
