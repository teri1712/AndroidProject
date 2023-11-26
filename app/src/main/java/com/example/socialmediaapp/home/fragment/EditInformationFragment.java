package com.example.socialmediaapp.home.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.apis.entities.requests.UpdateUserRequestBody;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.customview.button.RoundedButton;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.viewmodel.EditInformationViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.post.ImagePost;
import com.example.socialmediaapp.viewmodel.models.user.UserInformation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        // Required empty public constructor
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

    HomePage activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(EditInformationViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null) return null;

        root = inflater.inflate(R.layout.fragment_edit_information, container, false);
        submit_button = (RoundedButton) root.findViewById(R.id.submit_button);
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
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        MutableLiveData<UserInformation> userInfo = activity.getViewModel().getUserInfo();
        userInfo.observe(getViewLifecycleOwner(), new Observer<UserInformation>() {
            @Override
            public void onChanged(UserInformation information) {
                UpdateUserRequestBody user = new UpdateUserRequestBody();
                user.setFullname(information.getFullname());
                user.setAlias(information.getAlias());
                user.setGender(information.getGender());
                user.setBirthday(information.getBirthday());
                viewModel.getUserInfo().setValue(user);
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

        MutableLiveData<ImagePost> avatarPost = activity.getViewModel().getAvatarPost();
        MutableLiveData<ImagePost> backgroundPost = activity.getViewModel().getBackgroundPost();

        avatarPost.observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                if (imagePost == null) return;
                avatarButton.setBackgroundContent(imagePost.getImage(), 0);
            }
        });
        backgroundPost.observe(getViewLifecycleOwner(), new Observer<ImagePost>() {
            @Override
            public void onChanged(ImagePost imagePost) {
                if (imagePost == null) return;
                background.setImageDrawable(imagePost.getImage());
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
            }
        });
        viewModel.getPostSubmitState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("Success")) {
                    activity.finishFragment("edit information");
                } else if (s.equals("Failed")) {
                    viewModel.getPostSubmitState().setValue("Idle");
                }
            }
        });

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
                if (usernameValidation.getVisibility() == View.GONE) {
                    usernameValidation.setVisibility(View.VISIBLE);
                    usernameValidation.setText(status);
                }
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
                if (aliasValidation.getVisibility() == View.GONE) {
                    aliasValidation.setVisibility(View.VISIBLE);
                    aliasValidation.setText(status);
                }
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
                if (genderValidation.getVisibility() == View.GONE) {
                    genderValidation.setVisibility(View.VISIBLE);
                    genderValidation.setText(status);
                }
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
                if (birhtdayValidation.getVisibility() == View.GONE) {
                    birhtdayValidation.setVisibility(View.VISIBLE);
                    birhtdayValidation.setText(status);
                }
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


        initOnClick(root);
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


    private void initOnClick(View root) {
        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.postMyPost(getActivity());
            }
        });
        selectAvatarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.requestUpdateAvatar();
            }
        });
        selectBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.requestUpdateBackground();
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
                            }
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