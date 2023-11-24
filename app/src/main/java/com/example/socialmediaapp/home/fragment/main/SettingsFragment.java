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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View header_panel, header_frame;
    private ScrollView home_page_scroll;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_settings, container, false);
        header_panel = view.findViewById(R.id.header_panel);
        header_frame = view.findViewById(R.id.header_frame);
        home_page_scroll = (ScrollView) view.findViewById(R.id.home_scroll_pane);

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
        home_page_scroll.setOnTouchListener(new View.OnTouchListener() {
            float prey, prex;
            int cnt_head_page_scroll = 0;
            boolean spinner_dragged = false;
            boolean intercept_header_ani = false;
            int dist_for_header_appear = 0;
            float last_velo = 0;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                float y = event.getY();

                final float cur_trans_y = Math.abs(header_frame.getTranslationY());

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dist_for_header_appear = 0;
                        intercept_header_ani = false;
                        last_velo = 0;
                        break;
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP:
                        if (!intercept_header_ani) {
                            if (last_velo > 50) {
                                header_panel.animate().alpha(1f).setDuration(100).start();
                                header_frame.animate().translationY(0).setDuration(100).start();
                            }
                            break;
                        }
                        if (event.getAction() == MotionEvent.ACTION_UP)
                            home_page_scroll.getParent().requestDisallowInterceptTouchEvent(false);

                        intercept_header_ani = false;
                        if (last_velo > 50) {
                            header_panel.animate().alpha(1f).setDuration(100).start();
                            header_frame.animate().translationY(0).setDuration(100).start();
                            break;
                        }


                        if (cur_trans_y > header_frame.getHeight() / 2) {
                            header_panel.animate().alpha(0.0f).setDuration(100).start();
                            header_frame.animate().translationY(-header_frame.getHeight()).setDuration(100).start();
                        } else {
                            header_panel.animate().alpha(1f).setDuration(100).start();
                            header_frame.animate().translationY(0).setDuration(100).start();
                            home_page_scroll.post(new Runnable() {
                                @Override
                                public void run() {
                                    home_page_scroll.smoothScrollBy(0, (int) -cur_trans_y);
                                }
                            });
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float target_trans_head = 0;
                        float target_trans_command = 0;
                        if (y < prey) {
                            target_trans_head = Math.max(-header_frame.getHeight(), header_frame.getTranslationY() + y - prey);
                        } else if (y >= prey) {
                            target_trans_head = Math.min(0, header_frame.getTranslationY() + y - prey);
                        }
                        if (!intercept_header_ani) {
                            if (Math.abs(prex - event.getX()) / Math.abs(prey - event.getY()) > 0.8f || home_page_scroll.getHeight() >= home_page_scroll.getChildAt(0).getHeight()) {
                                dist_for_header_appear = 0;
                                break;
                            }
                            if (y > prey) dist_for_header_appear += y - prey;
                            boolean willIntercept = (cur_trans_y == 0 && target_trans_head != cur_trans_y);
                            willIntercept |= (cur_trans_y == header_frame.getHeight() && dist_for_header_appear >= 400);
                            if (!willIntercept) {
                                last_velo = Math.max(last_velo, y - prey);
                                break;
                            }
                            home_page_scroll.getParent().requestDisallowInterceptTouchEvent(true);
                            intercept_header_ani = true;
                            cnt_head_page_scroll = 0;
                        }

                        header_frame.setTranslationY(target_trans_head);
                        float alpha = 1 - Math.abs(header_frame.getTranslationY()) / header_frame.getHeight();
                        header_panel.setAlpha(alpha);
                        if (target_trans_head == -header_frame.getHeight()) {
                            intercept_header_ani = false;
                            dist_for_header_appear = 0;
                        }
                        break;
                    default:
                        break;
                }
                prey = event.getY();
                prex = event.getX();
                return false;
            }
        });

        return view;
    }
}