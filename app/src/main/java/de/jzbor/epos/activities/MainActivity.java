package de.jzbor.epos.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.NavListener;
import de.jzbor.epos.R;
import de.jzbor.epos.data.Schedule;
import de.jzbor.epos.data.dsb.DSBParser;
import de.jzbor.epos.data.dsb.DSBProvider;
import de.jzbor.epos.data.elternportal.ElternPortal;
import de.jzbor.epos.fragments.SubstitutionFragment;
import de.jzbor.epos.fragments.UpdatableFragment;
import de.jzbor.epos.threading.NextLessonThread;
import de.jzbor.epos.threading.UniHandler;

public class MainActivity extends AppCompatActivity
        implements SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "EpOs";
    private UpdatableFragment fragment;
    private SwipeRefreshLayout refreshLayout;
    private int loadingCount;
    private NavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingCount = 0;

        // Load GUI from sheets
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navView = findViewById(R.id.nav_view);
        NavListener nl = NavListener.getInstance();
        nl.init(this, drawer);
        navView.setNavigationItemSelectedListener(nl);

        // Create thread to update the next lesson display
        Handler nltHandler = new UniHandler(this);
        NextLessonThread nlt = new NextLessonThread(nltHandler, this);
        nlt.start();

        // Configure refresh layout
        // @TODO Replace RefreshLayout with a button in the ActionBar
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setOnRefreshListener(this);
        refreshLayout.setNestedScrollingEnabled(true);

        // Load personal details from cache
        try {
            String[] pers = (String[]) App.openObject(getApplicationContext().getCacheDir(), getString(R.string.filename_personal));
            View view = navView.getHeaderView(0);
            ((TextView) view.findViewById(R.id.student_name)).setText(pers[0]);
            ((TextView) view.findViewById(R.id.student_class)).setText(pers[1]);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Login "ElternPortal" api and dsb api
        try {
            String[] epLogin = (String[]) App.openObject(getApplicationContext().getCacheDir(), getString(R.string.filename_ep_login));
            ElternPortal.getInstance().login(epLogin[0], epLogin[1], epLogin[2]);
            String[] dsbLogin = (String[]) App.openObject(getApplicationContext().getCacheDir(), getString(R.string.filename_dsb_login));
            DSBProvider.login(dsbLogin[0], dsbLogin[1]);
            DSBParser.setFilter(dsbLogin[2]);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Create initial fragment
        fragment = new SubstitutionFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.commit();
        navView.setCheckedItem(R.id.nav_substitutions);


    }


    @Override
    protected void onResume() {
        super.onResume();

        // Load/Update next lesson label
        try {
            updateNextLesson();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Avoid bugs by refreshing selected item
        navView.setCheckedItem(fragment.getNavId());
    }

    @Override
    public void onBackPressed() {
        // Communicate back action to drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void updateNextLesson() throws IOException, ClassNotFoundException {
        // Update next lesson label
        Schedule schedule = (Schedule) App.openObject(getApplicationContext().getCacheDir(), getString(R.string.filename_schedule));
        updateNextLesson(schedule.nextLesson());
    }

    public void updateNextLesson(String str) {
        // Update next lesson label
        ((TextView) findViewById(R.id.main_next)).setText(str);
    }

    public void replaceFragment(UpdatableFragment f) {
        // Replace/Switch the content fragment
        fragment = f;
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onRefresh() {
        // Update current fragment
        if (ElternPortal.getInstance().loggedIn()) {
            refreshLayout.setRefreshing(true);
            fragment.doUpdate();
            refreshLayout.setRefreshing(false);
        } else {
            // Show error message on missing login
            Toast.makeText(this, getString(R.string.error_missing_login), Toast.LENGTH_SHORT).show();
            refreshLayout.setRefreshing(false);
        }
    }

    public void setRefreshing(boolean b) {
        // Set refreshing state of refresh layout
        refreshLayout.setRefreshing(b);
    }

    public void enableRefreshLayout(boolean b) {
        // @TODO Remove if unnecessary
        if (!refreshLayout.isRefreshing())
            refreshLayout.setEnabled(b);
    }

    public void setLoadingIcon(boolean b) {
        // Manage the visibility of the loading icon
        if (b)
            loadingCount++;
        else
            loadingCount--;
        if (loadingCount > 0)
            findViewById(R.id.toolbar_progress_bar).setVisibility(View.VISIBLE);
        else
            findViewById(R.id.toolbar_progress_bar).setVisibility(View.INVISIBLE);
    }

    public UpdatableFragment getFragment() {
        return fragment;
    }

    public void onUpdateFailed() {
        // Called by handler on failed update
        onUpdateFailed(getString(R.string.update_failed));
    }

    public void onUpdateFailed(String errorMsg) {
        // Called by handler on failed update
        setLoadingIcon(false);
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    public void onUpdateSucceeded() {
        // Called by handler on successful update
        try {
            fragment.loadCache();
            Toast.makeText(this, getString(R.string.update_succeeded), Toast.LENGTH_SHORT).show();
            updateNextLesson();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        setLoadingIcon(false);
    }
}
