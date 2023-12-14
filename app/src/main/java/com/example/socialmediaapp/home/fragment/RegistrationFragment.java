package com.example.socialmediaapp.home.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.RegistrationViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistrationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistrationFragment extends Fragment implements FragmentAnimation {


    public RegistrationFragment() {
        // Required empty public constructor
    }

    public static RegistrationFragment newInstance() {
        RegistrationFragment fragment = new RegistrationFragment();
        return fragment;
    }

    private View root;
    private RoundedButton submit_button;
    private CustomSpinningView spin;
    private RegistrationViewModel viewModel;
    private EditText usernameEditText, passwordEditText, retypePasswordEditText;
    private TextView usernameValidation, passwordValidation, retypePasswordValidation;
    private View authenProgress;

    private Activity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(RegistrationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) return null;

        root = inflater.inflate(R.layout.fragment_registration, container, false);
        submit_button = root.findViewById(R.id.submit_button);
        usernameEditText = root.findViewById(R.id.fullname);
        authenProgress = root.findViewById(R.id.spinner);
        passwordEditText = root.findViewById(R.id.password);
        retypePasswordEditText = root.findViewById(R.id.retype_password);
        usernameValidation = root.findViewById(R.id.usernameValidationStatus);
        passwordValidation = root.findViewById(R.id.passwordValidationStatus);
        retypePasswordValidation = root.findViewById(R.id.retypePasswordValidationStatus);

        MutableLiveData<String> username = viewModel.getUsername();
        MutableLiveData<String> password = viewModel.getPassword();
        MutableLiveData<String> retypePassword = viewModel.getRetypePassword();
        MutableLiveData<String> authenState = viewModel.getPostSubmitState();

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        username.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String invalidity = performValidationOnUsername(s);
                if (s.isEmpty() || invalidity == null) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                usernameValidation.setVisibility(View.VISIBLE);
                usernameValidation.setText(invalidity);
            }
        });
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                username.setValue(editable.toString());
            }
        });
        username.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, usernameEditText.getText().toString())) {
                    usernameEditText.setText(s);
                }
            }
        });

        password.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String invalidity = performValidationOnPassword(s);
                if (s.isEmpty() || invalidity == null) {
                    passwordValidation.setVisibility(View.GONE);

                    return;
                }
                passwordValidation.setVisibility(View.VISIBLE);
                passwordValidation.setText(invalidity);
            }
        });
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                password.setValue(editable.toString());
            }
        });
        password.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, passwordEditText.getText().toString())) {
                    passwordEditText.setText(s);
                }
            }
        });

        retypePassword.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String invalidity = performValidationOnRetypePassword(s);
                if (s.isEmpty() || invalidity == null) {
                    retypePasswordValidation.setVisibility(View.GONE);
                    return;
                }
                retypePasswordValidation.setVisibility(View.VISIBLE);
                retypePasswordValidation.setText(invalidity);
            }
        });
        retypePasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                retypePassword.setValue(editable.toString());
            }
        });
        retypePassword.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, retypePasswordEditText.getText().toString())) {
                    retypePasswordEditText.setText(s);
                }
            }
        });


        authenState.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("On registration")) {
                    authenProgress.setVisibility(View.VISIBLE);
                } else {
                    authenProgress.setVisibility(View.GONE);
                }
            }
        });
        authenState.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("On registration") || s.equals("Idle")) return;
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                if (s.equals("Success")) {
                    Intent intent = new Intent(activity, HomePage.class);
                    activity.startActivity(intent);
                    activity.finish();
                }

            }
        });

        activity = getActivity();
        initOnClick(root);
        usernameEditText.setText("username");
        passwordEditText.setText("password");
        retypePasswordEditText.setText("password");
        return root;

    }


    private String performValidationOnPassword(String s) {
        if (s.length() < 8 || s.length() > 16) {
            passwordValidation.setVisibility(View.VISIBLE);
            return "Password must have length from 8-16";
        }
        return null;
    }

    private String performValidationOnUsername(String s) {
        if (s.length() < 8 || s.length() > 16) {
            usernameValidation.setVisibility(View.VISIBLE);
            return "Username must have length from 8-16";
        }
        String p = "^[a-zA-Z0-9]*$"; // Adjust this regex according to your needs

        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(s);

        if (!m.matches()) {
            usernameValidation.setVisibility(View.VISIBLE);
            return "Username has invalid character";
        }
        return null;
    }

    private String performValidationOnRetypePassword(String s) {
        if (!Objects.equals(s, passwordEditText.getText().toString())) {
            retypePasswordValidation.setVisibility(View.VISIBLE);

            return "Retype password don't fit password";
        }
        return null;
    }

    private void initOnClick(View root) {
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] location = new int[2];
                    passwordEditText.getLocationOnScreen(location);
                    if (event.getRawX() >= (location[0] + passwordEditText.getWidth() - passwordEditText.getPaddingRight() - passwordEditText.getCompoundDrawables()[2].getBounds().width())) {
                        int password_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                        int normal_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                        if (passwordEditText.getInputType() == password_code) {
                            passwordEditText.setInputType(normal_code);
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show, 0);
                        } else {
                            passwordEditText.setInputType(password_code);
                            passwordEditText.setTypeface(ResourcesCompat.getFont(activity, R.font.roboto));
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        retypePasswordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] location = new int[2];
                    retypePasswordEditText.getLocationOnScreen(location);
                    if (event.getRawX() >= (location[0] + retypePasswordEditText.getWidth() - retypePasswordEditText.getPaddingRight() - retypePasswordEditText.getCompoundDrawables()[2].getBounds().width())) {
                        int password_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                        int normal_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                        if (retypePasswordEditText.getInputType() == password_code) {
                            retypePasswordEditText.setInputType(normal_code);
                            retypePasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show, 0);
                        } else {
                            retypePasswordEditText.setInputType(password_code);
                            retypePasswordEditText.setTypeface(ResourcesCompat.getFont(activity, R.font.roboto));
                            retypePasswordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                        }
                        return true;

                    }
                }
                return false;
            }
        });

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MutableLiveData<String> authenStatus = viewModel.getPostSubmitState();
                if (authenStatus.getValue().equals("On registration")) {
                    Toast.makeText(activity, "Please wait while authentication on progress", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (performValidationOnUsername(viewModel.getUsername().getValue()) != null || performValidationOnPassword(viewModel.getPassword().getValue()) != null || performValidationOnRetypePassword(viewModel.getRetypePassword().getValue()) != null) {
                    Toast.makeText(activity, "Ensure your fields is valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                authenStatus.setValue("On registration");
                viewModel.performSignUp();
            }
        });

    }


    @Override
    public void performEnd(Runnable endAction) {
        root.animate().translationX(root.getWidth()).setDuration(200).withEndAction(new Runnable() {
            @Override
            public void run() {
                endAction.run();
            }
        }).start();
    }

    @Override
    public void performStart() {
        root.setTranslationX(((View) root.getParent()).getWidth());
        root.animate().translationX(0).setDuration(300).start();
    }
}