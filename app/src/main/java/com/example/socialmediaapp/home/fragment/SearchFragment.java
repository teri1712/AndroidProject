package com.example.socialmediaapp.home.fragment;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.layoutviews.items.UserBasicInfoItem;
import com.example.socialmediaapp.viewmodel.HomePageViewModel;
import com.example.socialmediaapp.viewmodel.SearchFragmentViewModel;
import com.example.socialmediaapp.viewmodel.factory.ViewModelFactory;
import com.example.socialmediaapp.viewmodel.models.repo.Update;
import com.example.socialmediaapp.viewmodel.models.user.UserBasicInfo;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements FragmentAnimation {


    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    private SearchFragmentViewModel viewModel;
    private View root;
    private View back_button;
    private EditText searchEditText;
    private CircleButton eraseTextButton;
    private ImageView imageView;
    private CustomSpinningView resultSpin;
    private ViewGroup recentSearchContainer;
    private ViewGroup searchResultContainer;
    private View searchResultView, recenSearchView;
    private TextView lookupTextView;
    private HomePageViewModel vm;
    private TextView emptyTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this, new ViewModelFactory(this, null)).get(SearchFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        back_button = root.findViewById(R.id.back_button);
        eraseTextButton = root.findViewById(R.id.erase_text_button);
        searchEditText = root.findViewById(R.id.search_edit_text);
        resultSpin = root.findViewById(R.id.result_spin);
        recentSearchContainer = root.findViewById(R.id.recent_search_container);
        searchResultContainer = root.findViewById(R.id.search_result_container);
        lookupTextView = root.findViewById(R.id.lookup_textview);
        searchResultView = root.findViewById(R.id.search_result_view);
        recenSearchView = root.findViewById(R.id.recent_search_view);
        emptyTextView = root.findViewById(R.id.empty_textview);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        vm = ((HomePage) getActivity()).getViewModel();


        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    recenSearchView.setVisibility(View.VISIBLE);
                    searchResultView.setVisibility(View.GONE);
                } else {
                    if (searchResultView.getVisibility() == View.GONE) {
                        recenSearchView.setVisibility(View.GONE);
                        searchResultView.setVisibility(View.VISIBLE);
                    }
                    if (lookupTextView.getVisibility() == View.GONE) {
                        lookupTextView.setVisibility(View.VISIBLE);
                        resultSpin.setVisibility(View.VISIBLE);
                        emptyTextView.setVisibility(View.GONE);
                    }
                    searchResultContainer.removeAllViews();

                    String lookup = "Looking for ";
                    SpannableString span = new SpannableString(lookup + editable.toString());
                    span.setSpan(new StyleSpan(Typeface.BOLD), lookup.length(), lookup.length() + editable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lookupTextView.setText(span);

                    viewModel.loadSearchResult(getContext(), editable.toString());
                }
            }
        });

        initOnClick(root);
        initViewModel();
        return root;

    }

    public void initViewModel() {
        for (UserBasicInfo u : vm.getRecentSearchList().findAllItem()) {
            recentSearchContainer.addView(new UserBasicInfoItem(getContext(), u, "recent"));
        }
        vm.getRecentSearchList().getUpdateOnRepo().observe(getViewLifecycleOwner(), new Observer<Update<UserBasicInfo>>() {
            @Override
            public void onChanged(Update<UserBasicInfo> userBasicInfoUpdate) {
                if (userBasicInfoUpdate == null) return;
                Update.Op op = userBasicInfoUpdate.getOp();
                int pos = userBasicInfoUpdate.getPos();
                switch (op) {
                    case ADD:
                        UserBasicInfo u = vm.getRecentSearchList().getItem(pos);
                        recentSearchContainer.addView(new UserBasicInfoItem(getContext(), u, "recent"), recentSearchContainer.getChildCount() - pos);
                        break;
                    case REMOVE:
                        recentSearchContainer.removeViewAt(recentSearchContainer.getChildCount() - 1 - pos);
                        break;
                }
            }
        });
        viewModel.getSearchResult().observe(getViewLifecycleOwner(), new Observer<List<UserBasicInfo>>() {
            @Override
            public void onChanged(List<UserBasicInfo> userBasicInfos) {
                lookupTextView.setVisibility(View.GONE);
                resultSpin.setVisibility(View.GONE);
                if (searchResultView.getVisibility() == View.VISIBLE) {
                    if (userBasicInfos.isEmpty()) {
                        emptyTextView.setVisibility(View.VISIBLE);
                    }
                    for (UserBasicInfo user : userBasicInfos) {
                        searchResultContainer.addView(new UserBasicInfoItem(getContext(), user, "search"));
                    }
                }
            }
        });
    }

    private void initOnClick(View root) {
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        eraseTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchEditText.setText("");
            }
        });
    }


    @Override
    public void performEnd(Runnable endAction) {
        endAction.run();
    }

    @Override
    public void performStart() {
        searchEditText.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT);
    }
}