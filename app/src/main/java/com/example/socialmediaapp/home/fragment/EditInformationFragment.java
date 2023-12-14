package com.example.socialmediaapp.home.fragment;

import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.OnlineSessionHandler;
import com.example.socialmediaapp.application.session.SelfProfileSessionHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.application.session.UserSessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.EditInformationViewModel;
import com.example.socialmediaapp.viewmodel.UserSessionViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditInformationFragment extends Fragment implements FragmentAnimation {


    public EditInformationFragment() {
    }

    public static EditInformationFragment newInstance() {
        EditInformationFragment fragment = new EditInformationFragment();
        return fragment;
    }

    private EditInformationViewModel viewModel;
    private View root;
    private RoundedButton submit_button;
    private CircleButton avatarButton;
    private EditText fullname, alias, gender, birthday;
    private CustomSpinningView spin;
    private ImageView background;
    private EditText oldPassword, newPassword;
    private TextView usernameValidation, aliasValidation, genderValidation, birhtdayValidation;
    private CircleButton selectAvatarButton, selectBackgroundButton, datePickerButton;
    private HomePage activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new EditInformationViewModel();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_edit_information, container, false);

        submit_button = root.findViewById(R.id.submit_button);
        spin = root.findViewById(R.id.spinner);
        fullname = root.findViewById(R.id.fullname_edit_text);
        alias = root.findViewById(R.id.alias_edit_text);
        gender = root.findViewById(R.id.gender_edit_text);
        birthday = root.findViewById(R.id.birthday_edit_text);
        usernameValidation = root.findViewById(R.id.usernameValidationStatus);
        aliasValidation = root.findViewById(R.id.aliasValidationStatus);
        genderValidation = root.findViewById(R.id.genderValidationStatus);
        birhtdayValidation = root.findViewById(R.id.birthdayValidationStatus);

        background = root.findViewById(R.id.background_image);
        avatarButton = root.findViewById(R.id.avatar_button);


        oldPassword = root.findViewById(R.id.old_password_confirm);
        newPassword = root.findViewById(R.id.new_password);

        selectAvatarButton = root.findViewById(R.id.select_avatar_button);
        selectBackgroundButton = root.findViewById(R.id.select_background_button);
        datePickerButton = root.findViewById(R.id.date_picker_button);

        activity = (HomePage) getActivity();
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                performStart();
            }
        });

        UserSessionViewModel userSessionViewModel = activity.getViewModel();

        fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                viewModel.getFullname().setValue(editable.toString());
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
                viewModel.getAlias().setValue(editable.toString());
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
                viewModel.getBirthday().setValue(editable.toString());
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
                viewModel.getGender().setValue(editable.toString());
            }
        });
        userSessionViewModel.getAvatar().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                avatarButton.setBackgroundContent(new BitmapDrawable(getResources(), bitmap), 0);
            }
        });
        userSessionViewModel.getBackground().observe(getViewLifecycleOwner(), new Observer<Bitmap>() {
            @Override
            public void onChanged(Bitmap bitmap) {
                background.setImageDrawable(new BitmapDrawable(getResources(), bitmap));
            }
        });
        userSessionViewModel.getFullname().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                fullname.setText(s);
            }
        });
        userSessionViewModel.getAlias().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                alias.setText(s);
            }
        });
        userSessionViewModel.getGender().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                gender.setText(s);
            }
        });
        userSessionViewModel.getBirthday().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                birthday.setText(s);
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
                Toast.makeText(activity, s, Toast.LENGTH_SHORT).show();
                viewModel.getPostSubmitState().setValue("Idle");
            }
        });


        viewModel.getFullname().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null || s.isEmpty()) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                String status = performValidationOnFullname(s);
                if (status == null) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                usernameValidation.setText(status);
                usernameValidation.setVisibility(View.VISIBLE);
            }
        });
        viewModel.getAlias().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null || s.isEmpty()) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                String status = performValidationOnAlias(s);
                if (status == null) {
                    aliasValidation.setVisibility(View.GONE);
                    return;
                }
                aliasValidation.setVisibility(View.VISIBLE);
                aliasValidation.setText(status);
            }
        });
        viewModel.getGender().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null || s.isEmpty()) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                String status = performValidationOnGender(s);
                if (status == null) {
                    genderValidation.setVisibility(View.GONE);
                    return;
                }
                genderValidation.setVisibility(View.VISIBLE);
                genderValidation.setText(status);
            }
        });
        viewModel.getBirthday().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s == null || s.isEmpty()) {
                    usernameValidation.setVisibility(View.GONE);
                    return;
                }
                String status = performValidationOnBirthday(s);
                if (status == null) {
                    birhtdayValidation.setVisibility(View.GONE);
                    return;
                }
                birhtdayValidation.setVisibility(View.VISIBLE);
                birhtdayValidation.setText(status);
            }
        });

        OnlineSessionHandler.UserProfileProvider userProfileProvider = ApplicationContainer.getInstance().onlineSessionHandler.getUserProfileProvider();

        userProfileProvider.getSelfProfile().observe(getViewLifecycleOwner(), new Observer<SelfProfileSessionHandler>() {
            @Override
            public void onChanged(SelfProfileSessionHandler selfProfileSessionHandler) {
                initOnClick(selfProfileSessionHandler);
            }
        });
        return root;

    }

    private String performValidationOnAlias(String s) {
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
        if (s.length() < 3 || s.length() > 50) {
            return "Full name must have length from 3-100";
        }
        return null;
    }

    private String performValidationOnGender(String s) {
        if (s.equals("male") || s.equals("female")) return null;
        return "Gender must be either male or female";
    }

    private String performValidationOnBirthday(String s) {
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


    private void initOnClick(SelfProfileSessionHandler selfProfileSessionHandler) {
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediatorLiveData<String> postSubmitState = viewModel.getPostSubmitState();
                if (postSubmitState.getValue().equals("In progress")) {
                    Toast.makeText(getContext(), "Please wait", Toast.LENGTH_SHORT).show();
                    return;
                }
                postSubmitState.setValue("In progress");
                HashMap<String, String> data = new HashMap<>();
                data.put("fullname", viewModel.getFullname().getValue());
                data.put("alias", viewModel.getAlias().getValue());
                data.put("gender", viewModel.getGender().getValue());
                data.put("birthday", viewModel.getBirthday().getValue());

                LiveData<String> callBack = selfProfileSessionHandler.changeInformation(data);
                callBack.observe(getViewLifecycleOwner(), s -> postSubmitState.addSource(callBack, s1 -> {
                    postSubmitState.setValue(s1);
                    postSubmitState.removeSource(callBack);
                }));

            }
        });
        selectAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openUpdateAvatarFragment();
            }
        });
        selectBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.openUpdateBackgroundFragment();
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
                        (view1, year, month, day) -> {
                            String date = "";
                            date += Integer.toString(year) + "-";
                            date += Integer.toString(month) + "-";
                            date += Integer.toString(day);
                            birthday.setText(date);
                        }, y, m, d);
                datePickerDialog.show();
            }
        });

        oldPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] location = new int[2];
                    oldPassword.getLocationOnScreen(location);
                    if (event.getRawX() >= (location[0] + oldPassword.getWidth() - oldPassword.getPaddingRight() - oldPassword.getCompoundDrawables()[2].getBounds().width())) {
                        int password_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                        int normal_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                        if (oldPassword.getInputType() == password_code) {
                            oldPassword.setInputType(normal_code);
                            oldPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show, 0);
                            oldPassword.invalidate();
                        } else {
                            oldPassword.setInputType(password_code);
                            oldPassword.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
                            oldPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                            oldPassword.invalidate();
                        }
                        oldPassword.clearFocus();

                        return true;
                    }
                }
                return false;
            }
        });
        newPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int[] location = new int[2];
                    newPassword.getLocationOnScreen(location);
                    if (event.getRawX() >= (location[0] + newPassword.getWidth() - newPassword.getPaddingRight() - newPassword.getCompoundDrawables()[2].getBounds().width())) {
                        int password_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                        int normal_code = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL;
                        if (newPassword.getInputType() == password_code) {
                            newPassword.setInputType(normal_code);
                            newPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.show, 0);
                            newPassword.invalidate();
                        } else {
                            newPassword.setInputType(password_code);
                            newPassword.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto));
                            newPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.hide, 0);
                            newPassword.invalidate();
                        }
                        newPassword.clearFocus();
                        return true;
                    }
                }
                return false;
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