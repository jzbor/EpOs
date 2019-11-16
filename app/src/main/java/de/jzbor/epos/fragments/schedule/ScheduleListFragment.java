package de.jzbor.epos.fragments.schedule;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.jzbor.epos.R;
import de.jzbor.epos.fragments.ListFragment;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.model.Schedule;

public class ScheduleListFragment extends ListFragment<Schedule> {

    private static final String ARG_DAY = "day";
    private static final String TAG = "ScheduleListFragment";
    private RecyclerView recyclerView;
    private int day;
    private RecyclerView.OnScrollListener onScrollListener;

    public ScheduleListFragment() {
        super(ProviderManager.SCHEDULE, R.layout.fragment_schedule_list, R.string.filename_schedule);
    }

    public static ScheduleListFragment newInstance(Schedule schedule, int day) {
        ScheduleListFragment fragment = new ScheduleListFragment();
        Bundle additionalArgs = new Bundle();
        additionalArgs.putInt(ARG_DAY, day);
        return ListFragment.newInstance(schedule, new ScheduleListFragment(), additionalArgs);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!getArguments().isEmpty()) {
            day = getArguments().getInt(ARG_DAY);
        } else {
            TextView tv = new TextView(getContext());
            tv.setText("Error");
            return tv;
            // @TODO better handling of exception
        }

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected RecyclerView.Adapter createRecyclerViewAdapter() {
        return new MyScheduleRecyclerViewAdapter(dataObject, day);
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }

    public void setDay(Schedule schedule, int day) {
        this.dataObject = schedule;
        this.day = day;
        update();
    }

    public void update() {
        if (dataObject != null) {
            String[] scheduleDay = dataObject.getDays()[day];
            if ((scheduleDay != null) && (recyclerView != null)) {
                // Filter empty columns
                // Pass schedule day to new adapter
                recyclerView.setAdapter(new MyScheduleRecyclerViewAdapter(dataObject, day));
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

    @Override
    public int getNavId() {
        return R.id.nav_schedule;
    }
}
