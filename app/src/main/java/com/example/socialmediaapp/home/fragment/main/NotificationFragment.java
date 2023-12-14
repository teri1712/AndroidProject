package com.example.socialmediaapp.home.fragment.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.example.socialmediaapp.activitiy.HomePage;
import com.example.socialmediaapp.R;
import com.example.socialmediaapp.customview.progress.spinner.SpinningLoadPageView;
import com.example.socialmediaapp.layoutviews.items.NotifyItemView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View header_panel, header_frame;
    private ScrollView home_page_scroll;
    private SpinningLoadPageView load_spinner;
    private View head_fragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        header_panel = view.findViewById(R.id.header_panel);
        header_frame = view.findViewById(R.id.header_frame);
        home_page_scroll = (ScrollView) view.findViewById(R.id.home_scroll_pane);
        load_spinner = (SpinningLoadPageView) view.findViewById(R.id.load_spinner);
        head_fragment = view.findViewById(R.id.padding_head_fragment);

        home_page_scroll.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i1 == 0) {
                    header_panel.animate().alpha(1f).setDuration(100).start();
                    header_frame.animate().translationY(0).setDuration(100).start();
                }
            }
        });
        HomePage main_activity = (HomePage) (getActivity());


        ((ViewGroup) view.findViewById(R.id.notify_list_panel)).addView(new NotifyItemView(getContext()));
        return view;
    }
}