package de.jzbor.epos.threading;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InvalidClassException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.hgvinfo.DataHandler;
import de.jzbor.hgvinfo.model.Calendar;
import de.jzbor.hgvinfo.model.Notifications;
import de.jzbor.hgvinfo.model.Schedule;
import de.jzbor.hgvinfo.model.Subplan;

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
                else
                    activity.onUpdateFailed();
                // Missing else? onUpdateFailure?
                break;
            }
            case RESPONSE_SCHEDULE: {
                Schedule schedule = (Schedule) (msg.obj);
                try {
                    if (new File(activity.getCacheDir(), activity.getString(R.string.filename_schedule)).exists()) {
                        try {
                            Schedule oldSchedule = (Schedule) App.openObject(activity.getApplicationContext().getCacheDir(),
                                    activity.getString(R.string.filename_schedule));
                            schedule.setAdditionalClasses(oldSchedule.getAdditionalClasses());
                        } catch (InvalidClassException e) {
                            // This is necessary to avoid problems with old versions of the serialized schedule
                            Log.e(App.TAG, "Apparently the serialized schedule object was" +
                                    " created by an older version of the app");
                            e.printStackTrace();
                        }
                    }
                    //schedule.filter();
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_schedule), schedule);
                    activity.onUpdateSucceeded();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    activity.onUpdateFailed();
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
                    activity.onUpdateFailed();
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
                    activity.onUpdateFailed();
                }
                break;
            }
            case RESPONSE_NOTIFICATIONS: {
                Notifications notifications = (Notifications) (msg.obj);
                try {
                    App.saveObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_news), notifications);
                    activity.onUpdateSucceeded();
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.onUpdateFailed();
                }
                break;
            }
            case REPORT_NAME_CLASS: {
                try {
                    App.saveObject(activity.getCacheDir(), activity.getString(R.string.filename_personal), msg.obj);
                } catch (IOException e) {
                    e.printStackTrace();
                    activity.onUpdateFailed();
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
            default: {
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
        if (object != null) {
            System.out.println("Handle: " + type + " - " + id + " - " + object.getClass());
        } else {
            System.out.println("Handle: " + type + " - " + id + " - Object is null object: " + object);
        }
        Message msg = obtainMessage(type, object);
        msg.sendToTarget();
    }
}
