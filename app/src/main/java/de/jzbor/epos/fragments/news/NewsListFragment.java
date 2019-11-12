package de.jzbor.epos.fragments.news;

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
import de.jzbor.epos.fragments.UpdatableFragment;
import de.jzbor.epos.threading.UniHandler;
import de.jzbor.hgvinfo.DataProvider;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.dsb.DSBProvider;
import de.jzbor.hgvinfo.elternportal.EPProvider;
import de.jzbor.hgvinfo.model.Notifications;

public class NewsListFragment extends UpdatableFragment {

    private static final String ARG_NEWS = "news";
    private static final String TAG = "NewsListFragment";
    private RecyclerView recyclerView;
    private Notifications news;

    public NewsListFragment() {
    }

    public static NewsListFragment newInstance(Notifications news) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEWS, news);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // @TODO new view
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);

        if (getArguments() != null && !getArguments().isEmpty()) {
            news = (Notifications) getArguments().getSerializable(ARG_NEWS);
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
            recyclerView.setLayoutManager(new NewsListFragment.StaticLinearLayoutManager(context));
            // @TODO Improve - see ScheduleListFragment
            if (news != null)
                recyclerView.setAdapter(new MyNewsRecyclerViewAdapter(news));
            // update();
        }
        return view;
    }

    public void update() {
        if (news != null) {
            if (recyclerView != null) {
                recyclerView.setAdapter(new MyNewsRecyclerViewAdapter(news));
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
        if (App.inetReady((ConnectivityManager)
                Objects.requireNonNull(this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))));
        DataProvider provider = ProviderManager.getProvider(ProviderManager.NOTIFICATIONS, new DSBProvider(), new EPProvider());
        provider.requestNotifications(handler);
    }

    @Override
    public void loadCache() throws IOException, ClassNotFoundException {
        System.out.println("Load cache");
        news = (Notifications) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_news));
        if (recyclerView != null) {
            recyclerView.setAdapter(new MyNewsRecyclerViewAdapter(news));
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
