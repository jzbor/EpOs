package de.jzbor.epos.fragments.dates;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.elternportal.Dates;
import de.jzbor.epos.fragments.UpdatableFragment;
import de.jzbor.epos.threading.ComThread;
import de.jzbor.epos.threading.UniHandler;

public class DatesListFragment extends UpdatableFragment {

    private static final String ARG_DATES = "dates";
    private static final String TAG = "DatesListFragment";
    private RecyclerView recyclerView;
    private Dates dates;

    public DatesListFragment() {
    }

    public static DatesListFragment newInstance(Dates dates) {
        DatesListFragment fragment = new DatesListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATES, dates);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subplan_list, container, false);

        if (getArguments() != null && !getArguments().isEmpty()) {
            dates = (Dates) getArguments().getSerializable(ARG_DATES);
        } else {
            TextView tv = new TextView(getContext());
            tv.setText("Error");
            // return tv;
            // @TODO better handling of exception
            try {
                loadCache();
            } catch (IOException e) {
                e.printStackTrace();
                doUpdate();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        };

        // Set the adapter
        // Should always be true
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            // Set to own LayoutManager to suppress scrolling features
            recyclerView.setLayoutManager(new DatesListFragment.StaticLinearLayoutManager(context));
            // @TODO Improve - see ScheduleListFragment
            if (dates != null)
                recyclerView.setAdapter(new MyDatesRecyclerViewAdapter(dates));
            // update();
        }
        return view;
    }

    public void update() {
        if (dates != null) {
            if (recyclerView != null) {
                recyclerView.setAdapter(new MyDatesRecyclerViewAdapter(dates));
            } else if (isAdded()) {
                TextView tv = new TextView(getContext());
                tv.setText("Error");
                // @TODO better handling of exception
            }
        }
    }

    @Override
    public void doUpdate() {
        // Start update thread
        ((MainActivity) getActivity()).setLoadingIcon(true);
        UniHandler handler = new UniHandler(((MainActivity) this.getActivity()));
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        ComThread ct = new ComThread(cm, handler);
        ct.start(ComThread.WEB_SUBDIR_DATES);
        ((MainActivity) getActivity()).setRefreshing(false);
    }

    @Override
    public void loadCache() throws IOException, ClassNotFoundException {
        dates = (Dates) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_dates));
        if (recyclerView != null) {
            recyclerView.setAdapter(new MyDatesRecyclerViewAdapter(dates));
        }
    }

    /**
     * LayoutManager to suppress scrolling features
     */
    public class StaticLinearLayoutManager extends LinearLayoutManager {
        private boolean isScrollEnabled = true;

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
