package de.jzbor.epos.threading;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.activities.MainActivity;
import de.jzbor.epos.elternportal.Schedule;

public class NextLessonThread extends Thread {

    private Handler handler;
    private Activity activity;

    public NextLessonThread(Handler handler, MainActivity activity) {
        this.handler = handler;
        this.activity = activity;
    }

    @Override
    public void run() {
        // Update next lesson label
        try {
            Schedule schedule = (Schedule) App.openObject(activity.getApplicationContext().getCacheDir(), activity.getString(R.string.filename_schedule));
            Message msg = handler.obtainMessage(UniHandler.UPDATE_NEXT_LESSON, schedule.nextLesson());
            msg.sendToTarget();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        // Instruct handler to repeat
        handler.postDelayed(this, (1000 * 60 * 5));
    }
}
