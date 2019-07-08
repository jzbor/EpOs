package de.jzbor.epos.data.elternportal;

import android.net.ConnectivityManager;

import java.util.UUID;

import de.jzbor.epos.data.DataHandler;
import de.jzbor.epos.data.DataProvider;
import de.jzbor.epos.threading.EPThread;

public class EPProvider implements DataProvider {

    public static final boolean PROVIDES_SUBPLAN = true;
    public static final boolean PROVIDES_SCHEDULE = true;
    public static final boolean PROVIDES_CALENDAR = true;
    public static final boolean PROVIDES_NOTIFICATIONS = false;
    private ConnectivityManager connectivityManager;

    public EPProvider(ConnectivityManager connectivityManager){
        this.connectivityManager = connectivityManager;
    }

    @Override
    public int requestSubplan(DataHandler handler) {
        int id = UUID.randomUUID().hashCode();
        EPThread et = new EPThread(connectivityManager, handler, id);
        et.start(EPThread.WEB_SUBDIR_SUBPLAN);
        return id;
    }

    @Override
    public int requestSchedule(DataHandler handler) {
        int id = UUID.randomUUID().hashCode();
        EPThread et = new EPThread(connectivityManager, handler, id);
        et.start(EPThread.WEB_SUBDIR_SCHEDULE);
        return id;
    }

    @Override
    public int requestCalendar(DataHandler handler) {
        int id = UUID.randomUUID().hashCode();
        EPThread et = new EPThread(connectivityManager, handler, id);
        et.start(EPThread.WEB_SUBDIR_DATES);
        return id;
    }

    @Override
    public int requestNotifications(DataHandler handler) {
        return UUID.randomUUID().hashCode();
    }
}
