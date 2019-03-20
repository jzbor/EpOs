package de.jzbor.epos.threading;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.elternportal.Dates;
import de.jzbor.epos.elternportal.Schedule;
import de.jzbor.epos.elternportal.SubstituteDay;

public class UniHandler extends Handler {

    public static final int TOAST = 1;
    public static final int EP_RESPONSE_SUBPLAN = 2;
    public static final int EP_RESPONSE_SCHEDULE = 3;
    public static final int EP_RESPONSE_PERSONAL = 4;
    public static final int EP_RESPONSE_DATES = 5;
    public static final int EP_REPORT_NAME_CLASS = 6;
    public static final int UPDATE_NEXT_LESSON = 7;
    public static final int ERROR_UNKNOWN = 600;
    public static final int ERROR_EP_CONNECTION = 601;
    public static final int ERROR_EP_PARSING = 602;
    public static final int ERROR_EP_LOGIN = 603;
    public static final int ERROR_EP_SAVING = 604;
    public static final String TAG = "UniHandler";
    public MainActivity activity;

    public UniHandler(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case TOAST: {
                toast((String) msg.obj);
                break;
            }
            case EP_RESPONSE_SUBPLAN: {
                SubstituteDay[] sds = (SubstituteDay[]) (((Object[]) msg.obj)[0]);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_subplan_0), sds[0]);
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_subplan_1), sds[1]);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case EP_RESPONSE_SCHEDULE: {
                Schedule schedule = (Schedule) (((Object[]) msg.obj)[0]);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_schedule), schedule);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Message recMsg = obtainMessage(UPDATE_NEXT_LESSON, schedule.nextLesson());
                recMsg.sendToTarget();
                break;
            }
            case EP_RESPONSE_PERSONAL: {
                String[] pers = (String[]) (((Object[]) msg.obj)[0]);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_personal), pers);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case EP_RESPONSE_DATES: {
                Dates dates = (Dates) (((Object[]) msg.obj)[0]);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_dates), dates);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case EP_REPORT_NAME_CLASS: {
                try {
                    App.saveObject(activity.getCacheDir(), activity.getString(R.string.filename_personal), msg.obj);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case UPDATE_NEXT_LESSON: {
                activity.updateNextLesson((String) msg.obj);
                break;
            }
            case ERROR_UNKNOWN: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_EP_CONNECTION: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_EP_PARSING: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_EP_LOGIN: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_EP_SAVING: {
                activity.onUpdateFailed();
                break;
            }
        }
    }

    private void toast(String string) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
    }
}
