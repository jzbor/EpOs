package de.jzbor.epos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.hgvinfo.model.Schedule;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {
    // @TODO Save additional classes w/o schedule (if ep login is missing)

    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onClick(View view) {
        int srcId = view.getId();

        switch (srcId) {
            case (R.id.dsb_login_button): {
                Intent i = new Intent(this, DSBLoginActivity.class);
                this.startActivity(i);
                break;
            }
            case (R.id.ep_login_button): {
                Intent i = new Intent(this, EPLoginActivity.class);
                this.startActivity(i);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            schedule = (Schedule) App.openObject(getCacheDir(), getString(R.string.filename_schedule));
            String[] sarr = schedule.getAdditionalClasses();
            StringBuilder string = new StringBuilder();
            for (String s : sarr) {
                string.append(s).append(", ");
            }
            ((EditText) findViewById(R.id.additional_classes_input)).setText(string.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (schedule != null) {
            String[] sarr = ((EditText) findViewById(R.id.additional_classes_input)).getText().toString()
                    .replace(" ", "").split(",");
            schedule.setAdditionalClasses(sarr);
            try {
                App.saveObject(getCacheDir(), getString(R.string.filename_schedule), schedule);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
