package de.jzbor.epos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.jzbor.epos.R;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

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
}
