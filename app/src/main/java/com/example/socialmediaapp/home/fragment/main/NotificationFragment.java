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
                if (!intercept_header_ani) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            cnt_head_page_scroll = 0;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            float dif = y - prey;
                            dif /= (1 + 3 * load_spinner.getTranslationY() / (350));

                            if (spinner_dragged) {
                                float nxt_trans;
                                if (y < prey) {
                                    nxt_trans = Math.max(0.0f, load_spinner.getTranslationY() + dif);
                                } else {
                                    nxt_trans = Math.min(350, load_spinner.getTranslationY() + dif);
                                }
                                load_spinner.setTranslationY(nxt_trans);
                                load_spinner.setProgress((int) (310 * nxt_trans / (350)));
                                break;
                            }
                            if (Math.abs(prex - event.getX()) / Math.abs(prey - event.getY()) > 0.8f) {
                                cnt_head_page_scroll = 0;
                                break;
                            }

                            if (y < prey) {
                                cnt_head_page_scroll = Math.max(0, cnt_head_page_scroll - 1);
                            } else if (home_page_scroll.getScrollY() == 0) {
                                if (++cnt_head_page_scroll == 5 && load_spinner.getVisibility() == View.GONE) {
                                    load_spinner.setVisibility(View.VISIBLE);
                                    spinner_dragged = true;
                                    dist_for_header_appear = 0;
                                    home_page_scroll.getParent().requestDisallowInterceptTouchEvent(true);
                                }
                            }
                            break;
                        case MotionEvent.ACTION_CANCEL:
                        case MotionEvent.ACTION_UP:
                            if (spinner_dragged) {
                                spinner_dragged = false;
                                float progress = 100 * load_spinner.getTranslationY() / (350);
                                if (progress >= 70) {
                                    load_spinner.animate().translationY(250).setDuration(100).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            load_spinner.perfromLoadingAnimation();
                                        }
                                    }).start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Thread.sleep(3000);
                                            } catch (InterruptedException e) {
                                                throw new RuntimeException(e);
                                            }
                                            load_spinner.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    load_spinner.performEndLoadingAnimation();
                                                }
                                            });
                                        }
                                    }).start();
                                } else {
                                    load_spinner.setProgress(0);
                                    load_spinner.animate().translationY(0).setDuration(150).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            load_spinner.setVisibility(View.GONE);
                                        }
                                    }).start();
                                }
                                home_page_scroll.getParent().requestDisallowInterceptTouchEvent(false);
                            }
                            break;
                        default:
                            break;
                    }
                    if (spinner_dragged) {
                        prey = event.getY();
                        prex = event.getX();
                        return true;
                    }
                }

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


        ((ViewGroup) view.findViewById(R.id.notify_list_panel)).addView(new NotifyItemView(getContext()));
        return view;
    }
}