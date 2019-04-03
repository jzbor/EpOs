package de.jzbor.epos.elternportal;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Schedule implements Serializable {

    // @TODO Replace with xml strings
    private static final String[] WORKING_DAYS = {"Mo", "Di", "Mi", "Do", "Fr"};
    private static final String TAG = "mySchedule";
    private static final int DEFAULT_START_TIME = 7 * 60 + 55;
    private static final long FORECAST_TIME = 5 * 60 * 1000; // (millis)
    private static int staticStartTime;
    private int startTime;
    private String[][] days;
    private HashMap<String, String> classes;

    public Schedule(String[][] days) {
        this.days = days;
        startTime = DEFAULT_START_TIME;
        classes = new HashMap<>();
    }

    public Schedule(String html) throws ParserException {
        parse(html);
    }

    public static int nextWorkingDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // Shift day for compatibility
        day -= 2;
        // Adjust if weekend
        if ((day > 4) || (day < 0))
            day = -1;
        return day;
    }

    private static int[] getHM(Date date) {
        String now = new SimpleDateFormat("HH:mm").format(date);
        String[] sHM = now.split(":");
        int[] iHM = new int[2];
        for (int i = 0; i < sHM.length; i++) {
            iHM[i] = Integer.parseInt(sHM[i]);
        }
        return iHM;
    }

    // public int getLesson() {
    //     return getLesson(getHM(new Date()));
    // }

    public int getLesson(long millisBefore) {
        int[] time = getHM(new Date(System.currentTimeMillis() - millisBefore));
        return getLesson(time);
    }

    public int getLesson(int[] time) {
        int[][] beginnings = new int[15][];
        Log.d(TAG, "getLesson: now: " + Arrays.toString(time));
        for (int i = 0; i < 15; i++) {
            int beginnMins = beginOfLesson(i);
            beginnings[i] = new int[]{beginnMins / 60, beginnMins % 60};
        }
        for (int i = 0; i < beginnings.length; i++) {
            Log.d(TAG, "getLesson: beginning" + i + ": " + Arrays.toString(beginnings[i]));
            if ((time[0] < beginnings[i][0]) || ((time[0] == beginnings[i][0]) && (time[1] <= beginnings[i][1]))) {
                // Returns current lesson (with delay of 5 mins) (0 = before, 1 = first...)
                // Pretty difficult du decrypt
                return i;
            }
        }
        return -1;
    }

    public int beginOfLesson(int i) {
        // Beginning of lessons
        // @TODO Adapt for different beginnings
        int start = startTime;
        // Length of lesson
        int length = 45;
        // Addition for breaks
        int add = 0;
        if (i >= 2)
            add += 10;
        if (i >= 4)
            add += 20;
        if (i >= 6)
            add += 20;
        return start + i * length + add;
    }

    public int endOfLesson(int i) {
        return beginOfLesson(i) + 45;
    }

    public String lessonTime(int i) {
        int beginMins = beginOfLesson(i);
        int endMins = endOfLesson(i);
        int beginHours = beginMins / 60;
        int endHours = endMins / 60;
        beginMins %= 60;
        endMins %= 60;

        // Add '0' if mins < 10
        String sBeginMins = Integer.toString(beginMins);
        String sEndMins = Integer.toString(endMins);
        if (sBeginMins.length() == 1)
            sBeginMins = "0" + sBeginMins;
        if (sEndMins.length() == 1)
            sEndMins = "0" + sEndMins;
        return beginHours + ":" + sBeginMins + " - " + endHours + ":" + sEndMins;

    }

    public void parse(String html) throws ParserException {
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
        addClasses(html);
        try {
            addStartTime(html);
        } catch (ParserException e) {
            e.printStackTrace();
            startTime = DEFAULT_START_TIME;
        }
    }

    public String[] getDay(int i) {
        Log.d(TAG, "getDay: " + i);
        return days[i];
    }

    public void addClasses(String html) throws ParserException {
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
    }

    public void addStartTime(String html) throws ParserException {
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
            startTime = Integer.parseInt(time.split("\\.")[0]) * 60 + Integer.parseInt(time.split("\\.")[1]);
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
    }

    public void set(int day, int lesson, String str) {
        days[day][lesson] = str;
    }

    @Override
    public String toString() {
        // Parse schedule to a representative string
        String s = "";
        for (int i = 0; i < days.length; i++) {
            for (int j = 0; j < days[i].length; j++) {
                s += days[i][j] + "\n";
            }
            s += "----------\n";
        }
        return s;
    }

    public void filter() {
        // Abort if no classes loaded
        if (classes == null)
            return;
        // Iterate over days
        for (int i = 0; i < days.length; i++) {
            // Iterate over lessons
            for (int j = 0; j < days[i].length; j++) {
                // Skip empty items
                if (days[i][j].length() == 0)
                    continue;
                String str = "";
                // Separate rooms from classes
                String[] split = days[i][j].split(" ", 2);
                // Separate classes
                String[] tempClasses = split[0].split("/");
                // Separate rooms
                String[] rooms = split[1].split("/");
                // Iterate over input from schedule
                for (int k = 0; k < tempClasses.length; k++) {
                    // Iterate over classes
                    for (String c : classes.keySet()) {
                        // Compare them both
                        // @TODO fix bug (crossed out subject --> double subject)
                        if (tempClasses[k].contains(c)) {
                            str += tempClasses[k] + " " + rooms[k];
                        }
                    }
                }
                days[i][j] = str;
            }
        }
    }

    public boolean inClasses(String c) {
        // Checks whether a certain class is contained in classes
        for (String e : classes.keySet()) {
            if (e.equals(c))
                return true;
        }
        return false;
    }

    public String[][] getDays() {
        return days;
    }

    public String nextLesson() {
        // Returns a string representing the next lesson
        int nextLesson = getLesson(FORECAST_TIME);
        int day = Schedule.nextWorkingDay();
        Log.d(TAG, "nextLesson: day: " + day + " lesson: " + nextLesson);
        if (nextLesson < 0) {
            nextLesson = 0;
            day++;
            if (day > 4)
                day = 0;
        }
        if (day < 0) {
            day = 0;
            nextLesson = 0;
        }
        Log.d(TAG, "nextLesson: day: " + day + " lesson: " + nextLesson);
        while (!getDay(day)[nextLesson].matches(".*[a-zA-Z]+.*")) {
            nextLesson++;
            if (nextLesson >= getDay(day).length) {
                nextLesson = 0;
                day++;
            }
            if (day < 0 || 4 < day) {
                nextLesson = 0;
                day = 0;
            }
            Log.d(TAG, "nextLesson: day: " + day + " lesson: " + nextLesson + " str:" + getDay(day)[nextLesson]);
        }
        String lesson = getDay(day)[nextLesson];
        String prefix = Schedule.WORKING_DAYS[day] + " " + (nextLesson + 1) + ". Std.: ";
        return prefix + lesson;
    }
}
