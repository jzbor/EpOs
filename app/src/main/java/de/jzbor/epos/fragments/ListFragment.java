package de.jzbor.epos.fragments;

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
import java.io.Serializable;
import java.util.Objects;

import de.jzbor.epos.App;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.threading.UniHandler;
import de.jzbor.hgvinfo.DataProvider;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.dsb.DSBProvider;
import de.jzbor.hgvinfo.elternportal.EPProvider;

public abstract class ListFragment<T extends Serializable> extends UpdatableFragment {
    private static final String ARG_DATA_OBJECT = "data_object";
    protected T dataObject;
    private int dataType, layoutId, filenameId;
    private RecyclerView recyclerView;


    protected ListFragment(int dataType, int layoutId, int filenameId) {
        this.dataType = dataType;       // Probably ProviderManager.*
        this.layoutId = layoutId;
        this.filenameId = filenameId;
    }

    public static <DataType extends Serializable, FragmentType extends ListFragment<DataType>>
    FragmentType newInstance(DataType dataObject, FragmentType fragment) {
        return newInstance(dataObject, fragment, Bundle.EMPTY);
    }

    public static <DataType extends Serializable, FragmentType extends ListFragment<DataType>>
    FragmentType newInstance(DataType dataObject, FragmentType fragment, Bundle additionalArgs) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA_OBJECT, dataObject);
        args.putAll(additionalArgs);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(layoutId, container, false);

        if (getArguments() != null && !getArguments().isEmpty()) {
            dataObject = (T) getArguments().getSerializable(ARG_DATA_OBJECT);
        } else {
            TextView tv = new TextView(getContext());
            tv.setText("Error");
            // return tv;
            // @TODO better handling of exception

            try {
                loadCache();
            } catch (IOException e) {
                e.printStackTrace();
                this.triggerUpdate();
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
            recyclerView.setLayoutManager(new StaticLinearLayoutManager(context));
            // @TODO Improve - see ScheduleListFragment
            if (dataObject != null)
                recyclerView.setAdapter(createRecyclerViewAdapter());
            // update();
        }

        return view;
    }

    protected abstract RecyclerView.Adapter createRecyclerViewAdapter(); // e.g. new MyNewsRecyclerViewAdapter(dataObject)

    @Override
    public void triggerUpdate() {
        // Start update thread
        ((MainActivity) getActivity()).setLoadingIcon(true);
        UniHandler handler = new UniHandler(((MainActivity) this.getActivity()));
        if (App.inetReady((ConnectivityManager)
                Objects.requireNonNull(this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))))
            ;
        DataProvider provider = ProviderManager.getProvider(dataType, new DSBProvider(), new EPProvider());
        provider.requestNotifications(handler);
    }

    // @TODO Should it be included in UpdatableFragment?
    public void update() {
        if (dataObject != null) {
            if (recyclerView != null) {
                recyclerView.setAdapter(createRecyclerViewAdapter());
            } else if (isAdded()) {
                TextView tv = new TextView(getContext());
                tv.setText("Error");
                // @TODO better handling of exception
            }
        }
    }

    @Override
    public void loadCache() throws IOException, ClassNotFoundException {
        System.out.println("Load cache");
        dataObject = (T) App.openObject(getActivity().getApplicationContext().getCacheDir(),
                getString(filenameId));
        if (recyclerView != null) {
            recyclerView.setAdapter(createRecyclerViewAdapter());
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
