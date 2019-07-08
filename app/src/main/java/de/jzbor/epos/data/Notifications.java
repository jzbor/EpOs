package de.jzbor.epos.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Notifications implements Serializable {

    private ArrayList<Notification> notificationList;

    public Notifications() {
        notificationList = new ArrayList<>();
    }

    public Notifications(ArrayList<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    public void add(Notification notification) {
        notificationList.add(notification);
    }

    @Override
    public String toString() {
        String string = super.toString() + ":\n";

        for (Notification n :
                notificationList) {
            string += "\t" + n.getTitle() + "\n";
        }
        return string;
    }
}
