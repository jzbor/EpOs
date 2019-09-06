package de.jzbor.epos.data;

public interface DataHandler {

    int TOAST = 1;
    int RESPONSE_SUBPLAN = 2;
    int RESPONSE_SCHEDULE = 3;
    int RESPONSE_PERSONAL = 4;
    int RESPONSE_DATES = 5;
    int RESPONSE_NOTIFICATIONS = 6;
    int REPORT_NAME_CLASS = 7;
    int UPDATE_NEXT_LESSON = 8;
    int ERROR_UNKNOWN = 600;
    int ERROR_CONNECTION = 601;
    int ERROR_PARSING = 602;
    int ERROR_LOGIN = 603;
    int ERROR_SAVING = 604;

    void handle(int type, int id, Object object);
}
