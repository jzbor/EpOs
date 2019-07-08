package de.jzbor.epos.data;

import java.util.ArrayList;

public class Notifications {

    private ArrayList<Notification> notificationList;

    public Notifications() {
        notificationList = new ArrayList<>();
    }

    public void add(Notification notification) {

    }

    public class Notification {
        private String title;
        private String content;
    }
}
