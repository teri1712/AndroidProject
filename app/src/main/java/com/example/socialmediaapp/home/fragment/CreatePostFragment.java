package com.example.socialmediaapp.home.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.container.ClickablePanel;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodels.CreatePostViewModel;
import com.example.socialmediaapp.viewmodels.HomePageViewModel;
import com.example.socialmediaapp.viewmodels.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodels.models.UserSession;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.user.UserInformation;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreatePostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreatePostFragment extends Fragment implements FragmentAnimation {

    public CreatePostFragment() {
        // Required empty public constructor
    }

    public static CreatePostFragment newInstance() {
        CreatePostFragment fragment = new CreatePostFragment();
        return fragment;
    }

    private View media_picker_panel;
    private ImageView media_content;
    private CreatePostViewModel viewModel;
    private View root;

    private View back_button;
    private View select_picture_button;
    private View select_video_buton;
    private CircleButton avatar_button;
    private TextView fullname_textview;

    private ViewGroup media_container;
    private CircleButton remove_media_button;
    private ClickablePanel play_selected_video;
    private RoundedButton post_button;
    private EditText post_status_edit_text;
    private ActivityResultLauncher<String> pick_media_content;
    private CustomSpinningView spinner;
    private HomePage homePage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pick_media_content = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri == null) return;
                try {
                    String type = getContext().getContentResolver().getType(uri).split("/")[0];
                    media_content.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 600, getContext().getResources().getDisplayMetrics());
                    if (type.equals("image")) {
                        media_content.setImageURI(uri);
                        play_selected_video.setVisibility(View.GONE);
                    } else {
                        media_content.setImageDrawable(null);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                MediaMetadataRetriever r = new MediaMetadataRetriever();
                                r.setDataSource(getContext(), uri);
                                Bitmap thumbnail = r.getFrameAtTime(3000000);
                                BitmapDrawable drawable = new BitmapDrawable(getResources(), thumbnail);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        media_content.setImageDrawable(drawable);
                                    }
                                });
                            }
                        }).start();
                        play_selected_video.setVisibility(View.VISIBLE);
                    }
                    viewModel.setMediaType(type);
                    media_container.setVisibility(View.VISIBLE);
                    viewModel.getMediaContent().setValue(uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_create_post, container, false);
        avatar_button = root.findViewById(R.id.avatar_button);
        post_status_edit_text = root.findViewById(R.id.post_edit_text);
        media_content = root.findViewById(R.id.media_content);
        media_container = root.findViewById(R.id.media_container);
        remove_media_button = root.findViewById(R.id.remove_media_button);
        play_selected_video = root.findViewById(R.id.play_selected_video);
        media_picker_panel = root.findViewById(R.id.media_picker_panel);
        post_button = root.findViewById(R.id.post_button);
        fullname_textview = root.findViewById(R.id.fullname);
        spinner = root.findViewById(R.id.load_spinner);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup edit_panel = (ViewGroup) root.findViewById(R.id.edit_panel);
                View pad = new View(getContext());
                int w = ViewGroup.LayoutParams.MATCH_PARENT;
                int h = media_picker_panel.getHeight();
                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(w, h);
                pad.setLayoutParams(params);
                edit_panel.addView(pad);
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        homePage = (HomePage) getActivity();
        initViewModel();
        initOnClick(root);
        return root;

    }

    private void initViewModel() {
        HomePageViewModel homePageViewModel = homePage.getViewModel();
        homePageViewModel.getUserInfo().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation userSession) {
                fullname_textview.setText(userSession.getFullname());
            }
        });
        homePageViewModel.getAvatarPost().observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                if (imagePost == null) return;
                avatar_button.setBackgroundContent(imagePost.getImage(), 0);
            }
        });
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(CreatePostViewModel.class);
        viewModel.getCntEditedContent().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if (integer == 0) {
                    post_button.setTextContentColor(Color.parseColor("#adb5bd"));
                    post_button.setBackgroundColor(Color.argb(15, 0, 0, 0));
                    post_button.setClickedEnable(false);
                } else {
                    post_button.setTextContentColor(Color.parseColor("#757575"));
                    post_button.setBackgroundColor(Color.argb(30, 0, 0, 0));
                    post_button.setClickedEnable(true);
                }
                post_button.invalidate();
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("In progress")) {
                    spinner.setVisibility(View.VISIBLE);
                } else {
                    if (spinner.getVisibility() == View.VISIBLE) {
                        spinner.setVisibility(View.GONE);
                    }
                }
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Idle") || s.equals("In progress")) return;
                if (s.equals("Success")) {
                    homePage.finishFragment("create post");
                } else {
                    viewModel.getPostSubmitState().setValue("Idle");
                }
                Toast.makeText(homePage, s, Toast.LENGTH_SHORT).show();
            }
        });
        post_status_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getPostStatusContent().setValue(editable.toString());
            }
        });
    }

    private void initOnClick(View root) {
        remove_media_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.getMediaContent().setValue(null);
                media_container.setVisibility(View.GONE);
                viewModel.setMediaType(null);
            }
        });

        back_button = root.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        select_picture_button = root.findViewById(R.id.select_picture_button);
        select_picture_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_media_content.launch("image/*");
            }
        });

        select_video_buton = root.findViewById(R.id.select_video_button);
        select_video_buton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pick_media_content.launch("video/*");
            }
        });

        play_selected_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = viewModel.getMediaContent().getValue();
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setDataAndType(uri, "video/*");
                startActivity(intent);
            }
        });
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.postMyPost(getActivity());
            }
        });
    }


    @Override
    public void performEnd(Runnable endAction) {
        root.animate().translationY(root.getHeight()).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {
                endAction.run();
            }
        }).start();
    }

    @Override
    public void performStart() {
        View p = (View) getView().getParent();

        root.setTranslationY(p.getHeight() /2);
        root.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator()).start();
    }
}