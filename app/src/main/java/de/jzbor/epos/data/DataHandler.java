package de.jzbor.epos.data;

public interface DataHandler {

    public static final int TOAST = 1;
    public static final int RESPONSE_SUBPLAN = 2;
    public static final int RESPONSE_SCHEDULE = 3;
    public static final int RESPONSE_PERSONAL = 4;
    public static final int RESPONSE_DATES = 5;
    public static final int RESPONSE_NOTIFICATIONS = 6;
    public static final int REPORT_NAME_CLASS = 7;
    public static final int UPDATE_NEXT_LESSON = 8;
    public static final int ERROR_UNKNOWN = 600;
    public static final int ERROR_CONNECTION = 601;
    public static final int ERROR_PARSING = 602;
    public static final int ERROR_LOGIN = 603;
    public static final int ERROR_SAVING = 604;

    public void handle(int type, int id, Object object);
}
