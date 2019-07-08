package de.jzbor.epos.fragments.subplan;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.jzbor.epos.R;
import de.jzbor.epos.data.elternportal.SubstituteDay;

public class SubplanListFragment extends Fragment {

    private static final String ARG_SUBPLAN = "subplan";
    private static final String TAG = "DatesListFragment";
    private RecyclerView recyclerView;
    private SubstituteDay substituteDay;

    public SubplanListFragment() {
    }

    public static SubplanListFragment newInstance(SubstituteDay substituteDay) {
        SubplanListFragment fragment = new SubplanListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SUBPLAN, substituteDay);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subplan_list, container, false);

        if (!getArguments().isEmpty()) {
            substituteDay = (SubstituteDay) getArguments().getSerializable(ARG_SUBPLAN);
        } else {
            TextView tv = new TextView(getContext());
            tv.setText("Error");
            return tv;
            // @TODO better handling of exception
        }

        // Set the adapter
        // Should always be true
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            // Set to own LayoutManager to suppress scrolling features
            recyclerView.setLayoutManager(new SubplanListFragment.StaticLinearLayoutManager(context));
            // @TODO Improve - see ScheduleListFragment
            recyclerView.setAdapter(new MySubplanRecyclerViewAdapter(substituteDay));
            // update();
        }
        return view;
    }

    public void update() {
        if (substituteDay != null) {
            if (recyclerView != null) {
                recyclerView.setAdapter(new MySubplanRecyclerViewAdapter(substituteDay));
            } else if (isAdded()) {
                TextView tv = new TextView(getContext());
                tv.setText("Error");
                // @TODO better handling of exception
            }
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
