package de.jzbor.epos.data;

import java.io.Serializable;

public class Notification implements Serializable {
    private String title;
    private String content;

    public Notification(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
