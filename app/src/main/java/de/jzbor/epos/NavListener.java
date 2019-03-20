package de.jzbor.epos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import de.jzbor.epos.activities.InfoActivity;
import de.jzbor.epos.activities.LoginActivity;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.elternportal.Dates;
import de.jzbor.epos.fragments.ScheduleSuperFragment;
import de.jzbor.epos.fragments.SubstitutionFragment;
import de.jzbor.epos.fragments.dates.DatesListFragment;

public class NavListener implements NavigationView.OnNavigationItemSelectedListener {
    private static final NavListener ourInstance = new NavListener();
    private MainActivity activity;
    private DrawerLayout drawer;


    private NavListener() {
    }

    public static NavListener getInstance() {
        return ourInstance;
    }

    public void init(MainActivity activity, DrawerLayout drawer) {
        this.activity = activity;
        this.drawer = drawer;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        if (activity == null)
            return false;
        int id = item.getItemId();

        // Replace fragment/activity
        if (id == R.id.nav_substitutions) {
            activity.replaceFragment(new SubstitutionFragment());
        } else if (id == R.id.nav_schedule) {
            activity.replaceFragment(new ScheduleSuperFragment());
        } else if (id == R.id.nav_calendar) {
            activity.replaceFragment(new DatesListFragment());
        } else if (id == R.id.nav_login) {
            Intent i = new Intent(activity, LoginActivity.class);
            activity.startActivity(i);
        } else if (id == R.id.nav_info) {
            Intent i = new Intent(activity, InfoActivity.class);
            activity.startActivity(i);
        } /* else if (id == R.id.nav_dev) {
            Intent i = new Intent(activity, TestActivity.class);
            activity.startActivity(i);
        } */

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
