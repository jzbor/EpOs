package de.jzbor.epos.data.dsb;

import java.io.Serializable;


public class Subplan implements Serializable {

    private SubstituteDay[] substituteDays;
    private String timestamp;

    public Subplan(SubstituteDay[] substituteDays, String timestamp) {
        this.substituteDays = substituteDays;
        this.timestamp = timestamp;
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
}
