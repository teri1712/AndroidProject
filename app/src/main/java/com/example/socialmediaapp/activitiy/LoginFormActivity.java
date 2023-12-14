package com.example.socialmediaapp.activitiy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.DataAccessHandler;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.application.session.helper.PostAccessHelper;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.RegistrationFragment;
import com.example.socialmediaapp.viewmodel.LoginFormViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginFormActivity extends AppCompatActivity {

    private TextView forgetPasswordButton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox rememberMeCheckBox;
    private View oauth2Button;
    private View submitFormButton;
    private TextView signupButon;
    private TextView siginLogo;
    private TextView usernameValidation;
    private TextView passwordValidation;
    private View authenProgress;
    private ViewGroup root;
    private boolean waitForPopping = false;
    private LoginFormViewModel viewModel;

    private String performValidationOnPassword(String s) {
        if (s.length() < 8 || s.length() > 16) {
            return "Password must have length from 8-16";
        }
        return null;
    }

    private String performValidationOnUsername(String s) {
        if (s.length() < 8 || s.length() > 16) {
            return "Username must have length from 8-16";
        }
        String p = "^[a-zA-Z0-9]*$"; // Adjust this regex according to your needs

        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(s);

        if (!m.matches()) {
            return "Username has invalid character";
        }
        return null;
    }

    private void initViewObject() {
        root = (ViewGroup) ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);

        forgetPasswordButton = findViewById(R.id.forgetPassword);
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        rememberMeCheckBox = findViewById(R.id.rememberMe);
        oauth2Button = findViewById(R.id.oauth2Button);
        submitFormButton = findViewById(R.id.submit);
        signupButon = findViewById(R.id.signup);
        siginLogo = findViewById(R.id.signinTextView);
        usernameValidation = findViewById(R.id.usernameValidationStatus);
        passwordValidation = findViewById(R.id.passwordValidationStatus);
        authenProgress = findViewById(R.id.authenProgress);
        usernameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                }
                return false;
            }
        });
    }

    private void initTouchEvent() {
        signupButon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    signupButon.setPaintFlags(signupButon.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int val = signupButon.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG;
                    val ^= Paint.UNDERLINE_TEXT_FLAG;
                    signupButon.setPaintFlags(val);
                    signupButon.performClick();
                    return true;
                }
                return false;
            }
        });
        forgetPasswordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    forgetPasswordButton.setPaintFlags(forgetPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                    return true;
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    int val = forgetPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG;
                    val ^= Paint.UNDERLINE_TEXT_FLAG;
                    forgetPasswordButton.setPaintFlags(val);
                    forgetPasswordButton.performClick();
                    return true;
                }
                return false;
            }
        });
        submitFormButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MutableLiveData<String> authenStatus = viewModel.getAuthenticationState();
                if (authenStatus.getValue().equals("On authentication")) {
                    Toast.makeText(LoginFormActivity.this, "Please wait while authentication on progress", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (performValidationOnUsername(viewModel.getUsername().getValue()) != null || performValidationOnPassword(viewModel.getPassword().getValue()) != null) {
                    Toast.makeText(LoginFormActivity.this, "Ensure your fields is valid", Toast.LENGTH_SHORT).show();
                    return;
                }
                authenStatus.setValue("On authentication");
                viewModel.performAuthentication();
            }
        });
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
                            passwordEditText.setTypeface(ResourcesCompat.getFont(LoginFormActivity.this, R.font.roboto));
                            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                        }
                        return true;

                    }
                }
                return false;
            }
        });
        signupButon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(R.id.registration_fragment_container, RegistrationFragment.newInstance());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_form);
        initViewObject();
        initViewModel();
        usernameEditText.setText("username1");
        passwordEditText.setText("password1");
        initTouchEvent();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(LoginFormViewModel.class);
        MutableLiveData<String> username = viewModel.getUsername();
        MutableLiveData<String> password = viewModel.getPassword();
        MutableLiveData<String> authenState = viewModel.getAuthenticationState();
        username.observe(this, new Observer<String>() {
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
        username.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, usernameEditText.getText().toString())) {
                    usernameEditText.setText(s);
                }
            }
        });
        password.observe(this, new Observer<String>() {
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
        password.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, passwordEditText.getText().toString())) {
                    passwordEditText.setText(s);
                }
            }
        });
        authenState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("On authentication")) {
                    authenProgress.setVisibility(View.VISIBLE);
                } else {
                    authenProgress.setVisibility(View.GONE);
                }
            }
        });
        authenState.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("On authentication") || s.equals("Idle")) return;
                Toast.makeText(LoginFormActivity.this, s, Toast.LENGTH_SHORT).show();
                if (s.equals("Success")) {
                    SessionHandler sessionHandler = ApplicationContainer.getInstance().onlineSessionHandler.getUserSession();
                    Intent intent = new Intent(LoginFormActivity.this, HomePage.class);
                    intent.putExtra("session id", sessionHandler.getId());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {

        if (waitForPopping) return;
        Fragment top = findTopFragment();
        if (top instanceof FragmentAnimation) {
            waitForPopping = true;
            FragmentAnimation animation = (FragmentAnimation) top;
            animation.performEnd(new Runnable() {
                @Override
                public void run() {
                    View parent = (View) top.getView().getParent();
                    waitForPopping = false;
                    getSupportFragmentManager().popBackStackImmediate(top.getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    root.removeView(parent);
                }
            });
            return;
        }
        super.onBackPressed();
    }

    private Fragment findTopFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        int backStackLen = fragmentManager.getBackStackEntryCount();
        if (backStackLen > 0) {
            String tag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
            return fragmentManager.findFragmentByTag(tag);
        }
        return null;
    }


}