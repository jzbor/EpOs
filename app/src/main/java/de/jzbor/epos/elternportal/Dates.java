package de.jzbor.epos.elternportal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Dates implements Serializable {

    private static final String FORMAT = "dd.MM.yyyy";
    private Map<String, String> dates;

    public Dates(String html) {
        dates = new HashMap<>();
        parse(html);
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

    private void parse(String html) {
        Document document = Jsoup.parse(html);
        Element tbodyElement = document.getElementsByClass("table2").first().child(0);
        Elements trElements = tbodyElement.children();
        for (Element e :
                trElements) {
            System.out.println(e.children().size());
            if (e.children().size() == 3) {
                String date = e.child(0).text();
                String subject = e.child(2).text();
                dates.put(date, subject);
            }
        }
    }

    public Map<String, String> getDates() {
        return dates;
    }

    public Map<String, String> getDatesAfter(Date date){
        Map<String, String> map = new HashMap<>();
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
}
