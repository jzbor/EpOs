package de.jzbor.epos.data;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import de.jzbor.epos.App;

public class Subplan implements Serializable {

    private SubstituteDay[] substituteDays;
    private String timestamp;

    public Subplan(SubstituteDay[] substituteDays, String timestamp) {
        this.substituteDays = substituteDays;
        this.timestamp = timestamp;
    }

    public static Subplan load(File dir, String name) {
        try {
            return (Subplan) App.openObject(dir, name);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
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
