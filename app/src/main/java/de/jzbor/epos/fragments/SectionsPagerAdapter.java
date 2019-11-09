package de.jzbor.epos.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.RecyclerView;

import de.jzbor.epos.fragments.schedule.ScheduleListFragment;
import de.jzbor.hgvinfo.model.Schedule;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "SectionsPagerAdapter";
    private ScheduleListFragment[] fragments;
    private Schedule schedule;
    private ScheduleSuperFragment fragment;

    public SectionsPagerAdapter(ScheduleSuperFragment fragment, Schedule schedule) {
        super(fragment.getChildFragmentManager());
        this.fragment = fragment;
        this.schedule = schedule;
        fragments = new ScheduleListFragment[6];
        final RecyclerView.OnScrollListener scrollListener = new OnScrollListener();
        for (int i = 0; i < 5; i++) {
            // @TODO Sync ScrollBars
            fragments[i] = ScheduleListFragment.newInstance(schedule, i);
        }
    }

    @Override
    public Fragment getItem(int position) {
        if (schedule == null) {
            return new NotAvailableFragment();
        } else
            return fragments[position];
    }

    public void setScheduleDays(Schedule schedule) {
        for (int i = 0; i < 5; i++) {
            fragments[i].setDay(schedule, i);
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            for (Fragment f :
                    fragments) {
                if (f != null)
                    if (f.getView() != null)
                        f.getView().setScrollX(recyclerView.getScrollX());
            }
        }
    }
}
