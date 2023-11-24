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
import com.example.socialmediaapp.viewmodels.UpdateBackgroundViewModel;
import com.example.socialmediaapp.viewmodels.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodels.models.UserSession;
import com.example.socialmediaapp.viewmodels.models.post.ImagePost;
import com.example.socialmediaapp.viewmodels.models.user.UserInformation;

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

    private EditText post_status_edit_text;
    private ImageView imageView, simulate;
    private TextView fullname;
    private CustomSpinningView spin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            Uri imageUri = args.getParcelable("image uri");
            viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(UpdateBackgroundViewModel.class);
            viewModel.getImageUri().setValue(imageUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_update_background, container, false);
        post_status_edit_text = (EditText) root.findViewById(R.id.status_edit_text);
        save_button = (RoundedButton) root.findViewById(R.id.save_button);
        back_button = root.findViewById(R.id.back_button);
        imageView = root.findViewById(R.id.image_view);
        spin = root.findViewById(R.id.spinner);
        fullname = root.findViewById(R.id.fullname);
        simulate = root.findViewById(R.id.simulate_image_view);
        avatarButton = root.findViewById(R.id.avatar_button);
        HomePage homePage = (HomePage) getActivity();

        MutableLiveData<UserInformation> userInfo = homePage.getViewModel().getUserInfo();
        userInfo.observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation info) {
                fullname.setText(info.getFullname());
            }
        });

        MutableLiveData<ImagePost> avatarPost = homePage.getViewModel().getAvatarPost();
        avatarPost.observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                avatarButton.setBackgroundContent(imagePost.getImage(), 0);
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
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("In progress")) {
                    spin.setVisibility(View.VISIBLE);
                } else {
                    spin.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Idle") || s.equals("In progress"))
                    return;
                Toast.makeText(homePage, s, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    homePage.finishFragment("update background");
                } else if (s.equals("Failed")) {
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
    }
}