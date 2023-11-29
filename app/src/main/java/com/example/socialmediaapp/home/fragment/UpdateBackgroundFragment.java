package com.example.socialmediaapp.home.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.MainPostFragment;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UpdateBackgroundViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;
import com.google.android.material.internal.ManufacturerUtils;

import java.io.ObjectStreamClass;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateBackgroundFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateBackgroundFragment extends Fragment implements FragmentAnimation {


    public UpdateBackgroundFragment() {
        // Required empty public constructor
    }

    public static UpdateBackgroundFragment newInstance(Uri uri) {
        UpdateBackgroundFragment fragment = new UpdateBackgroundFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("image uri", uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    private UpdateBackgroundViewModel viewModel;
    private View root;
    private View back_button;
    private RoundedButton save_button;
    private CircleButton avatarButton;
    private HomePage homePage;
    private EditText post_status_edit_text;
    private ImageView imageView, simulate;
    private TextView fullname;
    private CustomSpinningView spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(UpdateBackgroundViewModel.class);
        Bundle args = getArguments();
        if (args != null) {
            Uri imageUri = args.getParcelable("image uri");
            viewModel.getImageUri().setValue(imageUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_update_background, container, false);
        post_status_edit_text = root.findViewById(R.id.status_edit_text);
        save_button = root.findViewById(R.id.save_button);
        back_button = root.findViewById(R.id.back_button);
        imageView = root.findViewById(R.id.image_view);
        spinner = root.findViewById(R.id.spinner);
        fullname = root.findViewById(R.id.fullname);
        simulate = root.findViewById(R.id.simulate_image_view);
        avatarButton = root.findViewById(R.id.avatar_button);
        homePage = (HomePage) getActivity();

        homePage.getViewModel().getUserInfo().observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation info) {
                fullname.setText(info.getFullname());
            }
        });

        homePage.getViewModel().getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), bitmap), 0);
            }
        });
        homePage.getViewModel().getBackground().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {

            }
        });
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.height = 360 * root.getWidth() / 640;
                params = simulate.getLayoutParams();
                params.height = 360 * root.getWidth() / 640;
                imageView.requestLayout();
                simulate.requestLayout();
            }
        });
        viewModel.getImageUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                imageView.setImageURI(uri);
                simulate.setImageURI(uri);
            }
        });
        viewModel.getPostStatusContent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, post_status_edit_text.getText().toString())) {
                    post_status_edit_text.setText(s);
                }
                viewModel.getPostStatusContent().removeObserver(this);
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Idle")) {
                    if (spinner.getVisibility() == View.VISIBLE) {
                        spinner.setVisibility(View.GONE);
                    }
                } else if (s.equals("In progress")) {
                    if (spinner.getVisibility() == View.GONE) {
                        spinner.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
                    viewModel.getPostSubmitState().setValue("Idle");
                }
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
        initOnClick(root);
        return root;

    }


    private void initOnClick(View root) {
        back_button = root.findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MutableLiveData<String> postSubmitState = viewModel.getPostSubmitState();
                if (postSubmitState.getValue().equals("In progress")) {
                    Toast.makeText(getContext(), "please wait until progress complete", Toast.LENGTH_SHORT).show();
                    return;
                }
                postSubmitState.setValue("In progress");
                Uri uri = viewModel.getImageUri().getValue();

                Bundle data = new Bundle();
                data.putString("status", viewModel.getPostStatusContent().getValue());
                data.putString("type", "background");
                data.putString("media content", (uri == null) ? null : uri.toString());
                MainPostFragment mainPostFragment = (MainPostFragment) (getActivity().getSupportFragmentManager().findFragmentByTag("post fragment"));
                PostFragment postFragment = (PostFragment) mainPostFragment.getChildFragmentManager().findFragmentByTag("posts");
                PostFragmentViewModel postFragmentViewModel = postFragment.getViewModel();
                postFragmentViewModel.uploadPost(data).observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        postSubmitState.setValue(s);
                        if (s.equals("Success")) {
                            homePage.finishFragment("update background");
                        }
                    }
                });
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
    }
}