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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodels.UpdateAvatarViewModel;
import com.example.socialmediaapp.viewmodels.factory.ViewModelFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateAvatarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateAvatarFragment extends Fragment implements FragmentAnimation {


    public UpdateAvatarFragment() {
        // Required empty public constructor
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

    private CustomSpinningView spin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            Uri imageUri = args.getParcelable("image uri");
            viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(UpdateAvatarViewModel.class);
            viewModel.getImageUri().setValue(imageUri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_update_avatar, container, false);
        post_status_edit_text = (EditText) root.findViewById(R.id.status_edit_text);
        save_button = (RoundedButton) root.findViewById(R.id.save_button);
        back_button = root.findViewById(R.id.back_button);
        imageView = root.findViewById(R.id.iamge_view);
        spin = root.findViewById(R.id.spinner);
        HomePage homePage = (HomePage) getActivity();

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ViewGroup.LayoutParams params = imageView.getLayoutParams();
                params.height = root.getWidth();
                imageView.requestLayout();
            }
        });
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
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
                    System.out.println("yess");
                    homePage.finishFragment("update avatar");
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