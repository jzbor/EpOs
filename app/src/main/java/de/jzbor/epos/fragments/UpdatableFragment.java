package de.jzbor.epos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;

public abstract class UpdatableFragment extends Fragment {
    private FragmentActivity activity;

    public abstract void triggerUpdate();

    public abstract void loadCache() throws IOException, ClassNotFoundException;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    public abstract int getNavId();
}
