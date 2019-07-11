package de.jzbor.epos.data;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import static de.jzbor.epos.activities.MainActivity.TAG;

public class ProviderManager {

    public static final int SUBPLAN = 1;
    public static final int SCHEDULE = 2;
    public static final int CALENDAR = 3;
    public static final int NOTIFICATIONS = 4;

    public static DataProvider getProvider(int type, DataProvider... providers) {
        for (int i = 0; i < providers.length; i++) {
            Log.d(TAG, "getProvider: Introducing(" + i + "): " + providers[i]);
            if (providers[i].available()) {
                switch (type) {
                    case SUBPLAN: {
                        if (providers[i].providesSubplan()) {
                            Log.d(TAG, "getProvider: " + providers[i]);
                            return providers[i];
                        }
                        break;
                    }
                    case SCHEDULE: {
                        if (providers[i].providesSchedule()) {
                            Log.d(TAG, "getProvider: " + providers[i]);
                            return providers[i];
                        }
                        break;
                    }
                    case CALENDAR: {
                        if (providers[i].providesCalendar()) {
                            Log.d(TAG, "getProvider: " + providers[i]);
                            return providers[i];
                        }
                        break;
                    }
                    case NOTIFICATIONS: {
                        if (providers[i].providesNotifications()) {
                            Log.d(TAG, "getProvider: " + providers[i]);
                            return providers[i];
                        }
                        break;
                    }
                }
            }
        }
        Log.d(TAG, "getProvider: " + providers[0] + " (End)");
        return providers[0];
    }

    public static boolean inetReady(ConnectivityManager connectivityManager) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return !((networkInfo == null) || (!networkInfo.isConnectedOrConnecting()));
    }
}
