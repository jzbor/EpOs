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
import android.widget.Toast;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.elternportal.ElternPortal;
import de.jzbor.epos.elternportal.SubstituteDay;
import de.jzbor.epos.fragments.subplan.SubplanListFragment;
import de.jzbor.epos.threading.ComThread;
import de.jzbor.epos.threading.UniHandler;

import static android.content.ContentValues.TAG;

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
        navId = R.id.nav_substitutions;
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
        try {
            loadCache();
        } catch (IOException | ClassNotFoundException e) {
            Log.i(TAG, "onActivityCreated: Unable to load cache");
            if (ElternPortal.getInstance().loggedIn()) {
                doUpdate();
            } else {
                Toast.makeText(this.getContext(), getString(R.string.error_missing_login), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void loadCache() throws IOException, ClassNotFoundException {
        SubstituteDay sd0 = ((SubstituteDay) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_subplan_0)));
        SubstituteDay sd1 = ((SubstituteDay) App.openObject(getActivity().getApplicationContext().getCacheDir(), getString(R.string.filename_subplan_1)));
        SubplanListFragment slf0 = SubplanListFragment.newInstance(sd0);
        SubplanListFragment slf1 = SubplanListFragment.newInstance(sd1);
        setListFragment(slf0, 0);
        setListFragment(slf1, 1);
        ((TextView) getView().findViewById(R.id.date0)).setText(sd0.getDate());
        ((TextView) getView().findViewById(R.id.date1)).setText(sd1.getDate());
    }

    @Override
    public void doUpdate() {
        // Start update thread
        ((MainActivity) getActivity()).setLoadingIcon(true);
        UniHandler handler = new UniHandler(((MainActivity) this.getActivity()));
        ConnectivityManager cm = (ConnectivityManager) this.getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        ComThread ct = new ComThread(cm, handler);
        ct.start("service/vertretungsplan");
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
