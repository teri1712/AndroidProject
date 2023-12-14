package com.example.socialmediaapp.customview.container;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socialmediaapp.R;

public class DragPanel extends FrameLayout {

    public interface DragHelper {
        boolean isTopBoundReached();

        boolean isBottomBoundReached();
    }

    public static class RecyclerViewDragHelper implements DragHelper {
        private LinearLayoutManager layoutManager;
        private RecyclerView.Adapter adapter;

        public RecyclerViewDragHelper(RecyclerView recyclerView) {
            layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            adapter = recyclerView.getAdapter();
        }

        @Override
        public boolean isTopBoundReached() {
            return layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
        }

        @Override
        public boolean isBottomBoundReached() {
            return layoutManager.findLastCompletelyVisibleItemPosition() == adapter.getItemCount() - 1;
        }
    }

    public static class ChildBoundDragHelper implements DragHelper {
        private View view;

        public ChildBoundDragHelper(View v) {
            view = v;
        }

        @Override
        public boolean isTopBoundReached() {
            ViewGroup parentOfView = (ViewGroup) view.getParent();
            int p[] = new int[2];
            view.getLocationInWindow(p);
            int t1 = p[1];
            parentOfView.getLocationInWindow(p);
            int t2 = p[1];
            return t1 == t2;
        }

        @Override
        public boolean isBottomBoundReached() {
            ViewGroup parentOfView = (ViewGroup) view.getParent();
            int p[] = new int[2];
            view.getLocationInWindow(p);
            int t1 = p[1];
            parentOfView.getLocationInWindow(p);
            int t2 = p[1];
            return t1 + view.getHeight() == t2 + parentOfView.getHeight();
        }
    }

    public interface DragListener {
        void onStartDrag();

        void onDrag();

        void onRelease();

        void onFinish();

        void onAboutToFinish();
    }

    public static class DragAdapter implements DragListener {
        public DragAdapter() {
        }

        @Override
        public void onStartDrag() {

        }

        @Override
        public void onDrag() {

        }

        @Override
        public void onRelease() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onAboutToFinish() {

        }
    }

    private DragListener dragListener;

    private DragHelper dragHelper;
    private int dragViewId;
    private View draggedView;
    private float prey, lastVelo;

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.my_container);
        try {
            dragViewId = a.getResourceId(R.styleable.my_container_dragged_view_id, -1);
        } finally {
            a.recycle();
        }
    }

    public void setDragHelper(DragHelper dragHelper) {
        this.dragHelper = dragHelper;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (dragViewId != -1) {
            draggedView = findViewById(dragViewId);
        }
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }


    public DragPanel(@NonNull Context context) {
        super(context);
    }

    public DragPanel(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public DragPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public DragPanel(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (dragHelper == null) return false;
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (dragListener != null) dragListener.onDrag();
                draggedView.setTranslationY(draggedView.getTranslationY() + y - prey);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                float curTrans = draggedView.getTranslationY();
                if (curTrans > draggedView.getHeight() / 4 || lastVelo > 50) {
                    if (dragListener != null) dragListener.onAboutToFinish();
                    draggedView.animate().translationY(draggedView.getHeight())
                            .setDuration(200)
                            .withEndAction(() -> {
                                if (dragListener != null) dragListener.onFinish();
                            }).start();
                } else if (curTrans < -draggedView.getHeight() / 4 || lastVelo < -50) {
                    if (dragListener != null) dragListener.onAboutToFinish();
                    draggedView.animate().translationY(-draggedView.getHeight())
                            .setDuration(200)
                            .withEndAction(() -> {
                                if (dragListener != null) dragListener.onFinish();

                            }).start();

                } else {
                    draggedView.animate().translationY(0)
                            .setDuration(200)
                            .setInterpolator(new DecelerateInterpolator())
                            .start();
                    if (dragListener != null) dragListener.onRelease();
                }
                return false;
        }
        lastVelo = y - prey;
        prey = y;
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (dragHelper == null) return false;
        float y = event.getY();
        boolean willIntercept = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (y > prey + 10 && dragHelper.isTopBoundReached()) {
                    willIntercept = true;
                }
                if (y < prey - 10 && dragHelper.isBottomBoundReached()) {
                    willIntercept = true;
                }
                break;
            default:
                break;
        }
        lastVelo = y - prey;
        prey = y;
        if (willIntercept && dragListener != null) {
            dragListener.onStartDrag();
        }
        return willIntercept;
    }

}
