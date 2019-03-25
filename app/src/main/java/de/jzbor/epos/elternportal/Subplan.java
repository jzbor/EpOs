package de.jzbor.epos.elternportal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import de.jzbor.epos.App;

public class Subplan implements Serializable {

    private SubstituteDay[] substituteDays;
    private String timestamp;

    public Subplan(String html) throws ParserException {
        substituteDays = SubstitutePlanParser.getSubstitutions(html);
        timestamp = parseTimestamp(html);
    }

    public static Subplan load(File dir, String name) {
        try {
            return (Subplan) App.openObject(dir, name);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String parseTimestamp(String html) {
        Document document = Jsoup.parse(html);
        Element superdiv = document.getElementsByClass("main_center").first();
        Element dateElement = superdiv.child(4); // Pretty crappy solution
        return dateElement.text();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public SubstituteDay[] getSubstituteDays() {
        return substituteDays;
    }

    public SubstituteDay getSubstituteDay(int i) {
        return substituteDays[i % 2];
    }

    public boolean save(File dir, String name) {
        try {
            App.saveObject(dir, name, this);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
