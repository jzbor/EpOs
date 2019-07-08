package de.jzbor.epos.fragments.schedule;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.jzbor.epos.R;
import de.jzbor.epos.data.Schedule;

public class ScheduleListFragment extends Fragment {

    private static final String ARG_SCHEDULE = "schedule";
    private static final String ARG_DAY = "day";
    private static final String TAG = "ScheduleListFragment";
    private RecyclerView recyclerView;
    private Schedule schedule;
    private int day;
    private RecyclerView.OnScrollListener onScrollListener;

    public ScheduleListFragment() {

    }

    public static ScheduleListFragment newInstance(Schedule schedule, int day) {
        ScheduleListFragment fragment = new ScheduleListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SCHEDULE, schedule);
        args.putInt(ARG_DAY, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_list, container, false);

        Log.d(TAG, "onCreateView: "+getArguments());
        if (!getArguments().isEmpty()) {
            schedule = (Schedule) getArguments().getSerializable(ARG_SCHEDULE);
            day = getArguments().getInt(ARG_DAY);
        } else {
            TextView tv = new TextView(getContext());
            tv.setText("Error");
            return tv;
            // @TODO better handling of exception
        }

        // Set the adapter
        // Should always be true
        if (view instanceof RecyclerView) {
            if (onScrollListener != null) {
                ((RecyclerView) view).clearOnScrollListeners();
                ((RecyclerView) view).addOnScrollListener(onScrollListener);
            }
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            // Set to own LayoutManager to suppress scrolling features
            recyclerView.setLayoutManager(new ScheduleListFragment.StaticLinearLayoutManager(context));
            recyclerView.setAdapter(new MyScheduleRecyclerViewAdapter(schedule, day));
            // update();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public void setDay(Schedule schedule, int day) {
        this.schedule = schedule;
        this.day = day;
        update();
    }

    public void update() {
        if (schedule != null) {
            String[] scheduleDay = schedule.getDays()[day];
            if ((scheduleDay != null) && (recyclerView != null)) {
                // Filter empty columns
                // Pass schedule day to new adapter
                recyclerView.setAdapter(new MyScheduleRecyclerViewAdapter(schedule, day));
            } else if (isAdded()) {
                TextView tv = new TextView(getContext());
                tv.setText("Error");
                // @TODO better handling of exception
            }
        }
    }

    public void addOnScrollListener(RecyclerView.OnScrollListener osl) {
        onScrollListener = osl;
        View view = getView();
        if (view != null && view instanceof RecyclerView) {
            ((RecyclerView) view).addOnScrollListener(osl);
        }
    }

    public void removeOnScrollListener(RecyclerView.OnScrollListener osl) {
        onScrollListener = osl;
        View view = getView();
        if (view != null && view instanceof RecyclerView) {
            ((RecyclerView) view).removeOnScrollListener(osl);
        }
    }

    public void scrollBy(int x, int y) {
        View view = getView();
        if (view != null && view instanceof RecyclerView) {
            view.scrollBy(x, y);
        }
    }

    /**
     * LayoutManager to suppress scrolling features
     */
    public class StaticLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = false;

        public StaticLinearLayoutManager(Context context) {
            super(context);
        }

        public void setScrollEnabled(boolean flag) {
            this.isScrollEnabled = flag;
        }

        @Override
        public boolean canScrollVertically() {
            //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
            return isScrollEnabled && super.canScrollVertically();
        }
    }
}
