package de.jzbor.epos.fragments;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.fragments.subplan.SubplanListFragment;
import de.jzbor.epos.threading.UniHandler;
import de.jzbor.hgvinfo.DataProvider;
import de.jzbor.hgvinfo.ProviderManager;
import de.jzbor.hgvinfo.dsb.DSBProvider;
import de.jzbor.hgvinfo.elternportal.EPProvider;
import de.jzbor.hgvinfo.model.Subplan;

import static de.jzbor.epos.activities.MainActivity.TAG;

public class SubstitutionFragment extends UpdatableFragment {

    public SubstitutionFragment() {
        // Required empty public constructor
    }

    public static SubstitutionFragment newInstance() {
        return new SubstitutionFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getNavId() {
        return R.id.nav_substitutions;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_substitution, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadCache();
    }

    public void loadCache() {
        Subplan subplan = Subplan.load(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_subplan));
        // @TODO better exception handling
        if (subplan == null)
            return;
        SubplanListFragment slf0 = SubplanListFragment.newInstance(subplan.getSubstituteDay(0));
        SubplanListFragment slf1 = SubplanListFragment.newInstance(subplan.getSubstituteDay(1));
        setListFragment(slf0, 0);
        setListFragment(slf1, 1);
        ((TextView) getView().findViewById(R.id.date0)).setText(subplan.getSubstituteDay(0).getDate());
        ((TextView) getView().findViewById(R.id.date1)).setText(subplan.getSubstituteDay(1).getDate());
        ((TextView) getView().findViewById(R.id.timestampView)).setText(subplan.getTimestamp());
    }

    @Override
    public void triggerUpdate() {
        // Start update thread
        ((MainActivity) getActivity()).setLoadingIcon(true);
        Log.d(TAG, "triggerUpdate: Refreshing false");
        UniHandler handler = new UniHandler(((MainActivity) this.getActivity()));
        if (App.inetReady((ConnectivityManager)
                Objects.requireNonNull(this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))))
            ;
        DataProvider provider = ProviderManager.getProvider(ProviderManager.SUBPLAN, new DSBProvider(), new EPProvider());
        provider.requestSubplan(handler);
    }

    public void setListFragment(SubplanListFragment subplanListFragment, int index) {
        // Do not set the ListFragment if the SubstitutionFragment is in the background
        if (isStateSaved())
            return;

        // Paste list fragments into UI
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        if (index % 2 == 0)
            transaction.replace(R.id.substitutions0, subplanListFragment);
        else
            transaction.replace(R.id.substitutions1, subplanListFragment);
        transaction.commit();
    }

}
