package de.jzbor.epos.fragments.news;

import android.support.v7.widget.RecyclerView;

import de.jzbor.epos.R;
import de.jzbor.epos.fragments.calendar.ListFragment;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.model.Notifications;

public class NewsListFragment extends ListFragment<Notifications> {

    public NewsListFragment() {
        super(ProviderManager.NOTIFICATIONS, R.layout.fragment_news_list, R.string.filename_news);
    }

    public static NewsListFragment newInstance(Notifications news) {
        return ListFragment.newInstance(news, new NewsListFragment());
    }

    @Override
    public int getNavId() {
        return R.id.nav_news;
    }

    @Override
    protected RecyclerView.Adapter createRecyclerViewAdapter() {
        return new MyNewsRecyclerViewAdapter(dataObject);
    }
}
