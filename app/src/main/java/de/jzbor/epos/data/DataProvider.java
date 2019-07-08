package de.jzbor.epos.data;

public interface DataProvider {

    public static final boolean PROVIDES_SUBPLAN = false;
    public static final boolean PROVIDES_SCHEDULE = false;
    public static final boolean PROVIDES_CALENDAR = false;
    public static final boolean PROVIDES_NOTIFICATIONS = false;

    public int requestSubplan(DataHandler handler);
    public int requestSchedule(DataHandler handler);
    public int requestCalendar(DataHandler handler);
    public int requestNotifications(DataHandler handler);

}
