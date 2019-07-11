package de.jzbor.epos.data;

public interface DataProvider {
    ;

    int requestSubplan(DataHandler handler);

    int requestSchedule(DataHandler handler);

    int requestCalendar(DataHandler handler);

    int requestNotifications(DataHandler handler);

    boolean available();

    boolean providesSubplan();

    boolean providesSchedule();

    boolean providesCalendar();

    boolean providesNotifications();

}
