package de.jzbor.epos.fragments.calendar;

import android.support.v7.widget.RecyclerView;

import de.jzbor.epos.R;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.model.Calendar;

public class DatesListFragment extends ListFragment<Calendar> {


    public DatesListFragment() {
        super(ProviderManager.CALENDAR, R.layout.fragment_dates_list, R.string.filename_dates);
    }

    public static DatesListFragment newInstance(Calendar calendar) {
        return ListFragment.newInstance(calendar, new DatesListFragment());
    }

    @Override
    public int getNavId() {
        return R.id.nav_calendar;
    }

    @Override
    protected RecyclerView.Adapter createRecyclerViewAdapter() {
        return new MyDatesRecyclerViewAdapter(dataObject);
    }
}
