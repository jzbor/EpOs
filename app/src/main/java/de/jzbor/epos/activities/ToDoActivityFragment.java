package de.jzbor.epos.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ToDoActivityFragment extends Fragment {

    public ToDoActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_to_do, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        String text;
        try {
            text = (String) App.openObject(getContext().getCacheDir(), "todo.str");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            text = "";
        }
        ((EditText) getView().findViewById(R.id.editorField)).setText(text);
    }

    @Override
    public void onPause() {
        super.onPause();

        String text = ((EditText) getView().findViewById(R.id.editorField)).getText().toString();
        try {
            App.saveObject(getContext().getCacheDir(), "todo.str", text);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
