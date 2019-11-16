package de.jzbor.epos.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.Objects;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.threading.UniHandler;
import de.jzbor.hgvinfo.DataProvider;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.dsb.DSBProvider;
import de.jzbor.hgvinfo.elternportal.EPProvider;
import de.jzbor.hgvinfo.elternportal.ElternPortal;
import de.jzbor.hgvinfo.model.Schedule;

public class ScheduleSuperFragment extends UpdatableFragment {
    private static final String TAG = "ScheduleSuperFragment";
    private SectionsPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private Schedule schedule;

    public ScheduleSuperFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getNavId() {
        return R.id.nav_schedule;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule_super, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            schedule = (Schedule) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_schedule));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            schedule = null;
        }

        // Load ViewPager
        loadVP();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Attempt to load cache or update otherwise
        try {
            loadCache();
        } catch (IOException | ClassNotFoundException e) {
            if (ElternPortal.getInstance().loggedIn()) {
                doUpdate();
            } else {
                Toast.makeText(this.getContext(), getString(R.string.error_missing_login), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadVP() {
        pagerAdapter = new SectionsPagerAdapter(this, schedule);
        viewPager = getView().findViewById(R.id.view_pager0);
        viewPager.setAdapter(pagerAdapter);
        TabLayout tabLayout = getView().findViewById(R.id.tabs);
        // Override refresh layout on action
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                ((MainActivity) getActivity()).enableRefreshLayout(state == ViewPager.SCROLL_STATE_IDLE);
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        // Move ViewPager to next/current working day
        viewPager.setCurrentItem(Schedule.nextWorkingDay());
    }

    @Override
    public void doUpdate() {
        // Start update thread
        ((MainActivity) getActivity()).setLoadingIcon(true);
        UniHandler handler = new UniHandler(((MainActivity) this.getActivity()));
        if (App.inetReady((ConnectivityManager)
                Objects.requireNonNull(this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))))
            ;
        DataProvider provider = ProviderManager.getProvider(ProviderManager.SCHEDULE, new DSBProvider(), new EPProvider());
        provider.requestSchedule(handler);
    }


    @Override
    public void loadCache() throws IOException, ClassNotFoundException {
        schedule = (Schedule) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_schedule));
        setDays(schedule);
    }

    public void setDays(Schedule schedule) {
        this.schedule = schedule;
        pagerAdapter.setScheduleDays(schedule);
    }
}
