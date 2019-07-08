package de.jzbor.epos.data.elternportal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import de.jzbor.epos.data.Calendar;
import de.jzbor.epos.data.Notification;
import de.jzbor.epos.data.Notifications;
import de.jzbor.epos.data.Schedule;
import de.jzbor.epos.data.Subplan;
import de.jzbor.epos.data.SubstituteDay;

public class EPParser {

    public static Calendar parseCalendar(String html) {
        Map<String, String>
                dates = new TreeMap<>(new Calendar.DateComparator());
        Document document = Jsoup.parse(html);
        Element tbodyElement = document.getElementsByClass("table2").first().child(0);
        Elements trElements = tbodyElement.children();
        for (Element e :
                trElements) {
            if (e.children().size() == 3) {
                String date = e.child(0).text();
                String subject = e.child(2).text();
                dates.put(date, subject);
            }
        }
        return new Calendar(dates);
    }

    public static Schedule parseSchedule(String html) throws ParserException {
        String[][] days;
        try {
            days = new String[5][];
            for (int i = 0; i < days.length; i++) {
                days[i] = new String[15];
            }
            Document document = Jsoup.parse(html);
            // Get table schedule
            Element table = document.getElementsByClass("table table-condensed table-bordered").first().child(0);
            Elements tableRows = table.children();
            for (int i = 0; i < 15; i++) {
                for (int j = 0; j < 5; j++) {
                    // Get cells (note the +1 for ignoring the time and day cells)
                    days[j][i] = tableRows.get(i + 1).child(j + 1).text();
                }
            }
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
        Map<String, String> classes = parseScheduleClasses(html);
        int startTime;
        try {
            startTime = parseScheduleStartTime(html);
        } catch (ParserException e) {
            e.printStackTrace();
            startTime = Schedule.DEFAULT_START_TIME;
        }
        return new Schedule(days, classes, startTime);
    }

    private static Map<String, String> parseScheduleClasses(String html) throws ParserException {
        Map<String, String> classes;
        try {
            Document document = Jsoup.parse(html);
            Element tbodyElement = document.getElementsByAttributeValue("id", "asam_content").first()    // <div>
                    .child(3)       // <table>
                    .child(0);      // <tbody>
            Element titleRowElement = tbodyElement.child(0);
            Element contentRowElement = tbodyElement.child(1);
            // Workaround for problem ("Schülergruppen vs Kurse")
            int i;
            for (i = 0; i < titleRowElement.children().size(); i++) {
                Element titleElement = titleRowElement.child(i);
                if (titleElement.text().contains("Kurs"))
                    break;
            }
            Element element = contentRowElement.child(i);
            String innerHtml = element.html();
            // Get classes (They're divided by "<br>")
            String[] origClasses = innerHtml.split("<br>");
            classes = new HashMap<>();
            for (String origClass : origClasses) {
                String[] temp = origClass.split(", ", 2);
                classes.put(temp[0], temp[1]);
            }
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
        return classes;
    }


    private static int parseScheduleStartTime(String html) throws ParserException {
        try {
            Document document = Jsoup.parse(html);
            Element tbodyElement = document
                    .getElementsByClass("table table-condensed table-bordered")
                    .first()        // <table>
                    .child(0);      // <tbody>
            Element tdElement = tbodyElement.child(1)   // <tr>
                    .child(0);      // <td>     content like "1<br>07.55 - 08.40"
            String times = tdElement.html().split("<br>")[1];
            String time = times.split(" - ")[0];
            return Integer.parseInt(time.split("\\.")[0]) * 60 + Integer.parseInt(time.split("\\.")[1]);
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
    }

    public static Subplan parseSubplan(String html) throws ParserException {
        SubstituteDay[] subDays = SubstitutePlanParser.getSubstitutions(html);
        String timestamp = parseSubplanTimestamp(html);
        return new Subplan(subDays, timestamp);
    }

    private static String parseSubplanTimestamp(String html) {
        Document document = Jsoup.parse(html);
        Element superdiv = document.getElementsByClass("main_center").first();
        Element dateElement = superdiv.child(4); // Pretty crappy solution
        return dateElement.text();
    }

    public static Notifications parseNotifications(String html) {
        Document document = Jsoup.parse(html);
        Element contentDiv = document.getElementById("asam_content");
        Elements listDaily = contentDiv.child(2).children();
        Elements listArchive = contentDiv.getElementsByClass("collapse");
        ArrayList<Notification> notifications = new ArrayList<>();
        for (Element entry :
                listDaily) {
            Element textElement = entry.child(0).child(0).child(0);
            String title = textElement.child(1).text();
            String content = textElement.child(2).text();
            notifications.add(new Notification(title, content));
        }
        for (Element entry :
                listArchive) {
            Element textElement = entry.child(0).child(0);
            String title = textElement.child(0).child(1).text();
            String content = textElement.child(1).text();
            notifications.add(new Notification(title, content));
        }
        return new Notifications(notifications);
    }
}
