package de.jzbor.epos.data;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class Calendar implements Serializable {

    private static final String FORMAT = "dd.MM.yyyy";
    private Map<String, String> dates;

    public Calendar(Map<String, String> dates) {
        this.dates = dates;
    }

    public Calendar() {
        dates = new TreeMap<>(new Calendar.DateComparator());
    }

    public static Date stringToDate(String string) {
        try {
            DateFormat format = new SimpleDateFormat(FORMAT, Locale.GERMAN);
            return format.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, String> getDates() {
        return dates;
    }

    public Map<String, String> getDatesAfter(Date date){
        Map<String, String> map = new TreeMap<>(new DateComparator());
        for (String key :
                dates.keySet()) {
            Date compareDate = stringToDate(key);
            if (compareDate != null && !compareDate.before(date)){
                map.put(key, dates.get(key));
            }
        }
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString() + "\n");
        for (String key :
                dates.keySet()) {
            sb.append("\t" + key + ": " + dates.get(key) + "\n");
        }
        return sb.toString();
    }

    public static class DateComparator implements Comparator<String>, Serializable {
        @Override
        public int compare(String s, String t1) {
            Date d1 = stringToDate(s);
            Date d2 = stringToDate(t1);
            if (!(d1 == null || d2 == null)) {
                return d1.compareTo(d2);
            } else {
                return 0;
            }
        }
    }
}
