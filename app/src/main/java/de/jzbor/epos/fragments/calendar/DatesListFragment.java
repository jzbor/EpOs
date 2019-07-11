package de.jzbor.epos.fragments.calendar;

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
import java.util.Objects;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.data.Calendar;
import de.jzbor.epos.data.DataProvider;
import de.jzbor.epos.data.ProviderManager;
import de.jzbor.epos.data.dsb.DSBProvider;
import de.jzbor.epos.data.elternportal.EPProvider;
import de.jzbor.epos.fragments.UpdatableFragment;
import de.jzbor.epos.threading.UniHandler;

public class DatesListFragment extends UpdatableFragment {

    private static final String ARG_DATES = "calendar";
    private static final String TAG = "DatesListFragment";
    private RecyclerView recyclerView;
    private Calendar calendar;

    public DatesListFragment() {
    }

    public static DatesListFragment newInstance(Calendar calendar) {
        DatesListFragment fragment = new DatesListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATES, calendar);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dates_list, container, false);

        if (getArguments() != null && !getArguments().isEmpty()) {
            calendar = (Calendar) getArguments().getSerializable(ARG_DATES);
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
        }

        // Set the adapter
        // Should always be true
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            // Set to own LayoutManager to suppress scrolling features
            recyclerView.setLayoutManager(new DatesListFragment.StaticLinearLayoutManager(context));
            // @TODO Improve - see ScheduleListFragment
            if (calendar != null)
                recyclerView.setAdapter(new MyDatesRecyclerViewAdapter(calendar));
            // update();
        }
        return view;
    }

    public void update() {
        if (calendar != null) {
            if (recyclerView != null) {
                recyclerView.setAdapter(new MyDatesRecyclerViewAdapter(calendar));
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
        if (ProviderManager.inetReady((ConnectivityManager)
                Objects.requireNonNull(this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))))
            ;
        DataProvider provider = ProviderManager.getProvider(ProviderManager.SUBPLAN, new DSBProvider(), new EPProvider());
        provider.requestSubplan(handler);
    }

    @Override
    public void loadCache() throws IOException, ClassNotFoundException {
        System.out.println("Load cache");
        calendar = (Calendar) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_dates));
        if (recyclerView != null) {
            recyclerView.setAdapter(new MyDatesRecyclerViewAdapter(calendar));
        }
        System.out.println("\tEnd load cache");
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
