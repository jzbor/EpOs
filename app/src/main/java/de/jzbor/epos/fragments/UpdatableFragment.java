package de.jzbor.epos.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import java.io.IOException;

public abstract class UpdatableFragment extends Fragment {
    protected int navId = 0;
    private FragmentActivity activity;

    public abstract void doUpdate();

    public abstract void loadCache() throws IOException, ClassNotFoundException;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    // @TODO wtf is navId
    public int getNavId() {
        return navId;
    }
}
