package de.jzbor.epos.widgets;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

import java.io.IOException;

import de.jzbor.epos.App;
import de.jzbor.epos.R;
import de.jzbor.epos.data.Schedule;

/**
 * Implementation of App Widget functionality.
 */
public class NextLessonWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.next_lesson_widget);
        // Load schedule
        try {
            Schedule schedule = (Schedule) App.openObject(context.getCacheDir(), context.getString(R.string.filename_schedule));
            widgetText = schedule.nextLesson();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // widgetText = e.getMessage();
            // widgetText = widgetText+"\n"+context.getCacheDir();
            widgetText = context.getText(R.string.not_available_long);
        }
        views.setTextViewText(R.id.appwidget_text, widgetText);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

