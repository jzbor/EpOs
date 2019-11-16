package de.jzbor.epos;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import de.jzbor.epos.activities.InfoActivity;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.activities.SettingsActivity;
import de.jzbor.epos.activities.ToDoActivity;
import de.jzbor.epos.fragments.ScheduleSuperFragment;
import de.jzbor.epos.fragments.SubstitutionFragment;
import de.jzbor.epos.fragments.calendar.DatesListFragment;
import de.jzbor.epos.fragments.news.NewsListFragment;

public class NavListener implements NavigationView.OnNavigationItemSelectedListener {
    private static final NavListener ourInstance = new NavListener();
    private MainActivity activity;
    private DrawerLayout drawer;
    private SubstitutionFragment substitutionFragment;
    private ScheduleSuperFragment scheduleSuperFragment;
    private NewsListFragment newsListFragment;
    private DatesListFragment datesListFragment;


    private NavListener() {
        substitutionFragment = new SubstitutionFragment();
        scheduleSuperFragment = new ScheduleSuperFragment();
        newsListFragment = new NewsListFragment();
        datesListFragment = new DatesListFragment();
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
            activity.replaceFragment(substitutionFragment);
        } else if (id == R.id.nav_schedule) {
            activity.replaceFragment(scheduleSuperFragment);
        } else if (id == R.id.nav_news) {
            activity.replaceFragment(newsListFragment);
        } else if (id == R.id.nav_calendar) {
            activity.replaceFragment(datesListFragment);
        } else if (id == R.id.nav_todo) {
            Intent i = new Intent(activity, ToDoActivity.class);
            activity.startActivity(i);
        } else if (id == R.id.nav_settings) {
            Intent i = new Intent(activity, SettingsActivity.class);
            activity.startActivity(i);
        } else if (id == R.id.nav_info) {
            Intent i = new Intent(activity, InfoActivity.class);
            activity.startActivity(i);
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
