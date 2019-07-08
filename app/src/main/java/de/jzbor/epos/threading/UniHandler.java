package de.jzbor.epos.threading;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.data.DataHandler;
import de.jzbor.epos.data.elternportal.Calendar;
import de.jzbor.epos.data.elternportal.Schedule;
import de.jzbor.epos.data.elternportal.Subplan;

public class UniHandler extends Handler implements DataHandler {


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
            case RESPONSE_SUBPLAN: {
                Subplan subplan = (Subplan) (msg.obj);
                boolean saved = subplan.save(activity.getApplicationContext().getCacheDir(),
                        activity.getString(R.string.filename_subplan));
                if (saved)
                    activity.onUpdateSucceeded();
                // Missing else? onUpdateFailure?
                break;
            }
            case RESPONSE_SCHEDULE: {
                Schedule schedule = (Schedule) (msg.obj);
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
            case RESPONSE_PERSONAL: {
                String[] pers = (String[]) (msg.obj);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_personal), pers);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case RESPONSE_DATES: {
                Calendar calendar = (Calendar) (msg.obj);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_dates), calendar);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
            case REPORT_NAME_CLASS: {
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
            case ERROR_CONNECTION: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_PARSING: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_LOGIN: {
                activity.onUpdateFailed();
                break;
            }
            case ERROR_SAVING: {
                activity.onUpdateFailed();
                break;
            }
        }
    }

    private void toast(String string) {
        Toast.makeText(activity, string, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void handle(int type, int id, Object object) {
        Message msg = obtainMessage(type, object);
        msg.sendToTarget();
    }
}
