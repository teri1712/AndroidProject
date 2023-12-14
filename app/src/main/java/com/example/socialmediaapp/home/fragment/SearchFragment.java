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
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.example.socialmediaapp.R;
import com.example.socialmediaapp.application.ApplicationContainer;
import com.example.socialmediaapp.application.session.RecentSearchAccessHandler;
import com.example.socialmediaapp.application.session.SessionHandler;
import com.example.socialmediaapp.customview.button.CircleButton;
import com.example.socialmediaapp.customview.progress.spinner.CustomSpinningView;
import com.example.socialmediaapp.home.fragment.animations.FragmentAnimation;
import com.example.socialmediaapp.home.fragment.main.RecentSearchFragment;
import com.example.socialmediaapp.layoutviews.items.UserBasicInfoItemView;
import com.example.socialmediaapp.viewmodel.SearchFragmentViewModel;
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

    public static SearchFragment newInstance(Bundle args) {
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public SearchFragmentViewModel getViewModel() {
        return viewModel;
    }

    private SearchFragmentViewModel viewModel;
    private View root;
    private View back_button;
    private EditText searchEditText;
    private CircleButton eraseTextButton;
    private CustomSpinningView resultSpin;
    private ViewGroup searchResultContainer;
    private View searchResultView, recentSearchView;
    private TextView lookupTextView;
    private TextView emptyTextView;
    private Integer sessionId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionId = getArguments().getInt("session id");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_search, container, false);
        back_button = root.findViewById(R.id.back_button);
        eraseTextButton = root.findViewById(R.id.erase_text_button);
        searchEditText = root.findViewById(R.id.search_edit_text);
        resultSpin = root.findViewById(R.id.result_spin);
        searchResultContainer = root.findViewById(R.id.search_result_container);
        lookupTextView = root.findViewById(R.id.lookup_textview);
        searchResultView = root.findViewById(R.id.search_result_view);
        recentSearchView = root.findViewById(R.id.recent_search_view);
        emptyTextView = root.findViewById(R.id.empty_textview);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                performStart();
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        initOnClick(root);
        initViewModel();
        return root;

    }

    public void initViewModel() {
        viewModel = new SearchFragmentViewModel(sessionId);
        viewModel.getLoadSearchResultState().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s.equals("idle")) {
                    resultSpin.setVisibility(View.GONE);
                    lookupTextView.setVisibility(View.GONE);
                } else {
                    searchResultContainer.removeAllViews();
                    lookupTextView.setVisibility(View.VISIBLE);
                    resultSpin.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                }
            }
        });
        viewModel.getSearchResult().observe(getViewLifecycleOwner(), new Observer<List<UserBasicInfo>>() {
            @Override
            public void onChanged(List<UserBasicInfo> userBasicInfos) {
                searchResultContainer.removeAllViews();
                if (userBasicInfos.isEmpty()) {
                    emptyTextView.setVisibility(View.VISIBLE);
                }
                for (UserBasicInfo userBasicInfo : userBasicInfos) {
                    searchResultContainer.addView(new UserBasicInfoItemView(SearchFragment.this, userBasicInfo));
                }
            }
        });
        viewModel.getSearchSessionHandler().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                sessionHandler.setRetain(true);
                initPostConstruct();
            }
        });
        viewModel.getRecentSearchSessionHandler().observe(getViewLifecycleOwner(), new Observer<SessionHandler>() {
            @Override
            public void onChanged(SessionHandler sessionHandler) {
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.recent_search_container, new RecentSearchFragment((RecentSearchAccessHandler) sessionHandler), "recent search");
                fragmentTransaction.commit();
            }
        });
    }

    private void initPostConstruct() {
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
                    recentSearchView.setVisibility(View.VISIBLE);
                    searchResultView.setVisibility(View.GONE);
                } else {
                    if (searchResultView.getVisibility() == View.GONE) {
                        recentSearchView.setVisibility(View.GONE);
                        searchResultView.setVisibility(View.VISIBLE);
                    }

                    String lookup = "Looking for ";
                    SpannableString span = new SpannableString(lookup + editable.toString());
                    span.setSpan(new StyleSpan(Typeface.BOLD), lookup.length(), lookup.length() + editable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    lookupTextView.setText(span);

                    viewModel.searchForUser(editable.toString());
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
        ApplicationContainer.getInstance().sessionRepository.deleteSession(sessionId);
        endAction.run();
    }

    @Override
    public void performStart() {
        searchEditText.requestFocus();
    }
}