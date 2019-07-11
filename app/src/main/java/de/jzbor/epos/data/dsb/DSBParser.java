package de.jzbor.epos.data.dsb;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class DSBParser {

    public static final String SUBPLAN_URL_KEY = "timetableurl";

    public static Map<String, String> parseSubplanInfo(String json) {
        json = json.substring(2, json.length() - 2);
        String[] kvpairs = json.split(",");
        Map<String, String> map = new HashMap<>();
        for (String kvpair :
                kvpairs) {
            String[] kvarr = kvpair.split("\":");
            String key = kvarr[0].replaceAll("\"", "");
            String val = kvarr[1].replaceAll("\"", "");
            map.put(key, val);
        }
        return map;
    }

    public static Subplan parseSubplan(String html) {
        Document document = Jsoup.parse(html);
        Elements titleElements = document.getElementsByClass("mon_title");
        Elements tableElements = document.getElementsByClass("mon_list");

        SubstituteDay[] subArr = new SubstituteDay[2];

        for (int i = 0; i < tableElements.size(); i++) {
            String title = titleElements.get(i).text();
            SubstituteDay subday = new SubstituteDay(title);
            Element tableRoot = tableElements.get(i).child(0);
            for (int j = 1; j < tableRoot.children().size(); j++) {
                Element rowElement = tableRoot.child(j);
                String[] sub = new String[]{
                        rowElement.child(1).text(),
                        rowElement.child(3).text(),
                        rowElement.child(2).text(),
                        rowElement.child(6).text(),
                        rowElement.child(4).text()
                };
                subday.addSubstitution(sub);
            }
            if (i < subArr.length) {
                subArr[i] = subday;
            }
        }
        String timestamp = "";
        String wholeText = document.text();
        System.out.println(wholeText);
        timestamp = wholeText.substring(wholeText.indexOf("Stand"), wholeText.length() - 1);
        String[] tss = timestamp.split(" ");
        timestamp = tss[0] + " " + tss[1] + " " + tss[2];

        return new Subplan(subArr, timestamp);
    }
}
