package com.example.socialmediaapp.home.fragment;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.customview.progress.dot.DotBlueProgress;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.progress.state.PipeView;
import com.example.socialmediaapp.customview.progress.state.StateView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.SetupInformationViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SetUpInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SetUpInformationFragment extends Fragment implements FragmentAnimation {


    public SetUpInformationFragment() {
        // Required empty public constructor
    }

    public static SetUpInformationFragment newInstance() {
        SetUpInformationFragment fragment = new SetUpInformationFragment();
        return fragment;
    }

    private View root;
    private RoundedButton next_button;
    private CustomSpinningView spin;
    private StateView step1_state, step2_state, step3_state;
    private PipeView step1_pipe, step2_pipe;

    private SetupInformationViewModel viewModel;
    private DotBlueProgress dotProgress;
    private TextView step1_text_view, step2_text_view, step3_text_view;

    private EditText fullname, alias, gender, birthday;
    private TextView usernameValidation, aliasValidation, genderValidation, birhtdayValidation;
    private View avatarFrame, infoFrame, recoveryFrame;
    private ImageView avatarImage;
    private HorizontalScrollView scroll;
    private CircleButton selectAvatarButton, datePickerButton;
    private ActivityResultLauncher<String> pickAvatar;
    private HomePage activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickAvatar = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri uri) {
                if (uri == null) return;
                viewModel.getAvatar().setValue(uri);
            }
        });
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(SetupInformationViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) return null;

        root = inflater.inflate(R.layout.setup_information_fragment, container, false);

        avatarFrame = root.findViewById(R.id.avatar_frame);
        infoFrame = root.findViewById(R.id.information_frame);
        recoveryFrame = root.findViewById(R.id.recovery_frame);
        scroll = root.findViewById(R.id.main_container);
        datePickerButton = root.findViewById(R.id.date_picker_button);
        avatarImage = root.findViewById(R.id.avatar_image);
        selectAvatarButton = root.findViewById(R.id.select_avatar_button);

        scroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                avatarFrame.getLayoutParams().width = scroll.getWidth();
                infoFrame.getLayoutParams().width = scroll.getWidth();
                recoveryFrame.getLayoutParams().width = scroll.getWidth();
                avatarFrame.requestLayout();
                infoFrame.requestLayout();
                recoveryFrame.requestLayout();
                root.setVisibility(View.GONE);
                root.post(new Runnable() {
                    @Override
                    public void run() {
                        //ultimate delay for smooth animation
                        performStart();
                    }
                });
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        activity = (HomePage) getActivity();

        spin = root.findViewById(R.id.spinner);
        step1_state = root.findViewById(R.id.step1_state);
        step1_pipe = root.findViewById(R.id.step1_pipe);
        step1_text_view = root.findViewById(R.id.step1_state_text_view);
        dotProgress = root.findViewById(R.id.progress_dot);
        next_button = root.findViewById(R.id.next_button);

        step1_state.setTextView(step1_text_view);
        step1_state.setPipe(step1_pipe);

        step2_state = root.findViewById(R.id.step2_state);
        step2_pipe = root.findViewById(R.id.step2_pipe);
        step2_text_view = root.findViewById(R.id.step2_state_text_view);
        step2_state.setTextView(step2_text_view);
        step2_state.setPipe(step2_pipe);

        step3_state = root.findViewById(R.id.step3_state);
        step3_text_view = root.findViewById(R.id.step3_state_text_view);
        step3_state.setTextView(step3_text_view);


        step1_pipe.setNextState(step2_state);
        step2_pipe.setNextState(step3_state);


        fullname = root.findViewById(R.id.fullname_edit_text);
        alias = root.findViewById(R.id.alias_edit_text);
        gender = root.findViewById(R.id.gender_edit_text);
        birthday = root.findViewById(R.id.birthday_edit_text);
        usernameValidation = root.findViewById(R.id.usernameValidationStatus);
        aliasValidation = root.findViewById(R.id.aliasValidationStatus);
        genderValidation = root.findViewById(R.id.genderValidationStatus);
        birhtdayValidation = root.findViewById(R.id.birthdayValidationStatus);


        viewModel.getCurSession().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                switch (s) {
                    case "avatar": {
                        MutableLiveData<Uri> sessionData = viewModel.getAvatar();
                        Observer<Uri> newObserver = new Observer<Uri>() {
                            @Override
                            public void onChanged(Uri uri) {
                            }
                        };
                        viewModel.setCurrentSessionDataObserver(newObserver);
                        sessionData.observe(getViewLifecycleOwner(), newObserver);
                        break;
                    }
                    case "information": {
                        MutableLiveData<UpdateUserRequestBody> sessionData = viewModel.getUserInfo();
                        Observer<UpdateUserRequestBody> newObserver = new Observer<UpdateUserRequestBody>() {
                            @Override
                            public void onChanged(UpdateUserRequestBody updateUserRequestBody) {
                                String fullname = updateUserRequestBody.getFullname();
                                String alias = updateUserRequestBody.getAlias();
                                String gender = updateUserRequestBody.getGender();
                                String birthday = updateUserRequestBody.getBirthday();

                                boolean ok = (performValidationOnFullname(fullname) == null);
                                ok = ok & (performValidationOnAlias(alias) == null);
                                ok = ok & (performValidationOnGender(gender) == null);
                                ok = ok & (performValidationOnBirthday(birthday) == null);

                                next_button.setClickedEnable(ok);
                            }
                        };
                        viewModel.getAvatar().removeObserver((Observer<? super Uri>) viewModel.getCurrentSessionDataObserver());
                        viewModel.setCurrentSessionDataObserver(newObserver);
                        sessionData.observe(getViewLifecycleOwner(), newObserver);

                        break;
                    }
                    case "recovery": {
                        next_button.setTextContent("Finish");
                        MutableLiveData<Object> sessionData = viewModel.getRecovery();
                        Observer<Object> newObserver = new Observer<Object>() {
                            @Override
                            public void onChanged(Object o) {
                            }
                        };
                        viewModel.getUserInfo().removeObserver((Observer<? super UpdateUserRequestBody>) viewModel.getCurrentSessionDataObserver());
                        sessionData.observe(getViewLifecycleOwner(), newObserver);
                        break;
                    }
                }
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
                if (s.equals("Idle") || s.equals("In progress"))
                    return;
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                if (s.equals("Success")) {
                    spin.setVisibility(View.GONE);
                    step3_state.switchToInProgress();
                    performEnd(new Runnable() {
                        @Override
                        public void run() {
                            FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.remove(SetUpInformationFragment.this);
                            fragmentTransaction.commit();
                            ViewGroup pContainer = (ViewGroup) container.getParent();
                            pContainer.removeView(container);
                            activity.getViewModel().loadHomePageContent(activity);
                        }
                    });
                } else {
                    viewModel.getPostSubmitState().setValue("Idle");
                }
            }
        });
        viewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Uri>() {
            @Override
            public void onChanged(Uri uri) {
                avatarImage.setImageURI(uri);
            }
        });
        UpdateUserRequestBody userInfo = new UpdateUserRequestBody();
        userInfo.setFullname("");
        userInfo.setAlias("");
        userInfo.setGender("");
        userInfo.setBirthday("");
        viewModel.getUserInfo().setValue(userInfo);
        viewModel.getCurSession().setValue("avatar");


        fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getUserInfo().getValue().setFullname(editable.toString());
                viewModel.getUserInfo().setValue(viewModel.getUserInfo().getValue());
            }
        });
        alias.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getUserInfo().getValue().setAlias(editable.toString());
                viewModel.getUserInfo().setValue(viewModel.getUserInfo().getValue());
            }
        });
        birthday.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getUserInfo().getValue().setBirthday(editable.toString());
                viewModel.getUserInfo().setValue(viewModel.getUserInfo().getValue());
            }
        });
        gender.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getUserInfo().getValue().setGender(editable.toString());
                viewModel.getUserInfo().setValue(viewModel.getUserInfo().getValue());
            }
        });


        viewModel.getFullname().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, fullname.getText().toString())) {
                    fullname.setText(s);
                }
            }
        });
        viewModel.getAlias().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, alias.getText().toString())) {
                    alias.setText(s);
                }
            }
        });
        viewModel.getGender().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, gender.getText().toString())) {
                    gender.setText(s);
                }

            }
        });
        viewModel.getBirthday().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (!Objects.equals(s, birthday.getText().toString())) {
                    birthday.setText(s);
                }
            }
        });


        viewModel.getFullname().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String status = performValidationOnFullname(s);
                if (status == null || status.isEmpty()) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                usernameValidation.setVisibility(View.VISIBLE);
                usernameValidation.setTextColor(Color.parseColor("#B00020"));
                usernameValidation.setText(status);
            }
        });
        viewModel.getAlias().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String status = performValidationOnAlias(s);
                if (status == null || status.isEmpty()) {
                    aliasValidation.setVisibility(View.GONE);
                    return;
                }
                aliasValidation.setTextColor(Color.parseColor("#B00020"));
                aliasValidation.setVisibility(View.VISIBLE);
                aliasValidation.setText(status);

            }
        });
        viewModel.getGender().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String status = performValidationOnGender(s);
                if (status == null || status.isEmpty()) {
                    genderValidation.setVisibility(View.GONE);
                    return;
                }
                genderValidation.setTextColor(Color.parseColor("#B00020"));
                genderValidation.setVisibility(View.VISIBLE);
                genderValidation.setText(status);

            }
        });
        viewModel.getBirthday().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                String status = performValidationOnBirthday(s);
                if (status == null || status.isEmpty()) {
                    birhtdayValidation.setVisibility(View.GONE);
                    return;
                }

                birhtdayValidation.setTextColor(Color.parseColor("#B00020"));
                birhtdayValidation.setVisibility(View.VISIBLE);
                birhtdayValidation.setText(status);
            }
        });

        initOnClick(root);
        step1_state.switchToInProgress();


        return root;

    }

    private String performValidationOnAlias(String s) {
        if (s.isEmpty()) {
            return "";
        }
        if (s.length() < 2 || s.length() > 16) {
            return "Alias must have length from 2-16";
        }
        String p = "^[a-zA-Z0-9]*$"; // Adjust this regex according to your needs

        Pattern pattern = Pattern.compile(p);
        Matcher m = pattern.matcher(s);

        if (!m.matches()) {
            return "Alias has invalid character (a-z A-Z 0-9)";
        }
        return null;
    }

    private String performValidationOnFullname(String s) {
        if (s.isEmpty()) {
            return "";
        }
        if (s.length() < 3 || s.length() > 50) {
            return "Full name must have length from 3-100";
        }
        return null;
    }

    private String performValidationOnGender(String s) {
        if (s.isEmpty()) {
            return "";
        }
        if (s.equals("male") || s.equals("female")) return null;
        return "Gender must be either male or female";
    }

    private String performValidationOnBirthday(String s) {
        if (s.isEmpty()) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);
        String status = null;
        try {
            sdf.parse(s);
        } catch (Exception e) {
            status = "Birthday must in form yyyy-MM-dd";
        }
        return status;
    }

    private void initOnClick(View root) {
        selectAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAvatar.launch("image/*");
            }
        });
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nxt = viewModel.getCurSession().getValue();
                if (nxt.equals("avatar")) {
                    nxt = "information";
                    step1_state.swithToCompleted();
                    dotProgress.nextPage();
                    ObjectAnimator.ofInt(scroll, "scrollX", scroll.getWidth() + scroll.getScrollX()).setDuration(200).start();
                } else if (nxt.equals("information")) {
                    nxt = "recovery";
                    step2_state.swithToCompleted();
                    dotProgress.nextPage();
                    ObjectAnimator.ofInt(scroll, "scrollX", scroll.getWidth() + scroll.getScrollX()).setDuration(200).start();
                } else {
                    viewModel.postMyPost(getContext());
                    return;
                }

                viewModel.getCurSession().setValue(nxt);

            }
        });
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar c = Calendar.getInstance();
                int y = c.get(Calendar.YEAR);
                int m = c.get(Calendar.MONTH);
                int d = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                String date = "";
                                date += Integer.toString(year) + "-";
                                date += Integer.toString(month) + "-";
                                date += Integer.toString(day);

                                viewModel.getUserInfo().getValue().setBirthday(date);
                                viewModel.getUserInfo().setValue(viewModel.getUserInfo().getValue());
                                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(birthday.getWindowToken(), 0);
                            }
                        }, y, m, d);
                datePickerDialog.show();
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
        root.setVisibility(View.VISIBLE);
        View p = (View) getView().getParent();
        root.setTranslationY(p.getHeight() * 66 / 100);
        root.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        root.animate().translationY(0).setDuration(300).setInterpolator(new DecelerateInterpolator()).withEndAction(new Runnable() {
            @Override
            public void run() {
                root.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        }).start();
    }
}