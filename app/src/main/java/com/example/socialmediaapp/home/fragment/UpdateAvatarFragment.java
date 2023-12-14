package com.example.socialmediaapp.home.fragment;

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
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.MainPostFragment;
import com.example.socialmediaapp.home.fragment.main.PostFragment;
import com.example.socialmediaapp.layoutviews.items.PostItemView;
import com.example.socialmediaapp.viewmodel.PostFragmentViewModel;
import com.example.socialmediaapp.viewmodel.UpdateAvatarViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.google.android.material.internal.ManufacturerUtils;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateAvatarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateAvatarFragment extends Fragment implements FragmentAnimation {

    public UpdateAvatarFragment() {
    }

    public static UpdateAvatarFragment newInstance(Uri uri) {
        UpdateAvatarFragment fragment = new UpdateAvatarFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("image uri", uri);
        fragment.setArguments(bundle);
        return fragment;
    }

    private UpdateAvatarViewModel viewModel;
    private View root;
    private View back_button;
    private RoundedButton save_button;
    private EditText post_status_edit_text;
    private ImageView imageView;
    private HomePage homePage;
    private CustomSpinningView spinner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(UpdateAvatarViewModel.class);

        Bundle args = getArguments();
        if (args != null) {
            Uri imageUri = args.getParcelable("image uri");
            viewModel.getImageUri().setValue(imageUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_update_avatar, container, false);
        post_status_edit_text = root.findViewById(R.id.status_edit_text);
        save_button = root.findViewById(R.id.save_button);
        back_button = root.findViewById(R.id.back_button);
        imageView = root.findViewById(R.id.iamge_view);
        spinner = root.findViewById(R.id.spinner);
        back_button = root.findViewById(R.id.back_button);

        homePage = (HomePage) getActivity();

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.height = root.getWidth();
                imageView.requestLayout();
            }
        });
        viewModel.getImageUri().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                imageView.setImageURI(uri);
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
        initOnClick();
        return root;

    }

    @Override
    public void onStart() {
        super.onStart();
        performStart();
    }

    private void initOnClick() {
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
                    Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
                    return;
                }
                postSubmitState.setValue("In progress");

                Uri uri = viewModel.getImageUri().getValue();
                Bundle data = new Bundle();
                data.putString("post content", viewModel.getPostStatusContent().getValue());
                data.putString("type", "avatar");
                data.putString("media content", uri.toString());
                homePage.updateAvatar(data).observe(getViewLifecycleOwner(), s -> {
                    postSubmitState.setValue(s);
                    if (s.equals("Success")) {
                        homePage.finishFragment("update avatar");
                    }
                });
            }
        });
    }


    @Override
    public void performEnd(Runnable endAction) {
        root.animate().translationY(root.getHeight()).setDuration(200).withEndAction(() -> endAction.run()).start();
    }

    @Override
    public void performStart() {
        post_status_edit_text.requestFocus();
    }
}