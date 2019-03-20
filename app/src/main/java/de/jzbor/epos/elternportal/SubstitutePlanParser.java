package de.jzbor.epos.elternportal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class SubstitutePlanParser {
    private static final String URL = "service/vertretungsplan";
    private static final String NO_SUBS = "Keine Vertretungen";
    private static final String[] EMPTY_SUBS = {"", "", "Keine Vertretungen", "", ""};

    public static List<String> extractSubstitutions(String html) {
        // Get substitutions assuming they're all contained in '<tr>' tags
        Document document = Jsoup.parse(html);
        return document.getElementsByTag("tr").eachText();
    }

    public static String extractDate(String html, int dateIndex) throws ParserException {
        try {
            if (dateIndex > 1) dateIndex = 1;
            else if (dateIndex < 0) dateIndex = 0;
            Document document = Jsoup.parse(html);
            Element element = document.getElementsByClass("main_center").first();
            element = element.getElementsByClass("list bold full_width text_center").get(dateIndex);
            return element.text();
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
    }

    public static SubstituteDay[] getSubstitutions(String html) throws ParserException {
        try {
            Document document = Jsoup.parse(html);
            // superdiv = div containing both tables
            Element superdiv = document.getElementsByClass("main_center").first();
            // dates = headlines with dates
            Elements dates = superdiv.getElementsByClass("list bold full_width text_center");
            // subTables = tables with substitutions
            Elements subTables = superdiv.getElementsByClass("table");
            // Parsing subDay0
            List<String[]> subDay0 = new ArrayList<>();
            // Check whether there are subs
            if (subTables.get(0).text().contains(NO_SUBS)) {
                subDay0.add(EMPTY_SUBS);
            } else {
                for (Element sub : subTables.get(0).child(0).children()) {
                    String[] tempArr = new String[5];
                    // Add cells as attributes
                    for (int j = 0; j < tempArr.length; j++) {
                        tempArr[j] = sub.children().get(j).text();
                    }
                    // Sort out declaration row
                    if (!tempArr[0].equals("Std."))
                        subDay0.add(tempArr);
                }
            }
            // Parsing subDay1
            List<String[]> subDay1 = new ArrayList<>();
            // Check whether there are subs
            if (subTables.get(1).text().contains(NO_SUBS)) {
                subDay1.add(EMPTY_SUBS);
            } else {
                for (Element sub : subTables.get(1).child(0).children()) {
                    String[] tempArr = new String[5];
                    // Add cells as attributes
                    for (int j = 0; j < tempArr.length; j++) {
                        tempArr[j] = sub.children().get(j).text();
                    }
                    // Sort out declaration row
                    if (!tempArr[0].equals("Std."))
                        subDay1.add(tempArr);
                }
            }
            // Pack return array
            SubstituteDay[] result = new SubstituteDay[2];
            result[0] = new SubstituteDay(subDay0, dates.get(0).text());
            result[1] = new SubstituteDay(subDay1, dates.get(1).text());
            return result;
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
    }

    public static String[] parseNameClass(String html) throws ParserException {
        // Retrieve name and class of student
        try {
            Document document = Jsoup.parse(html);
            Element container = document.getElementsByClass("col-sm-6 text-right").first();
            Element textField = container.getElementsByAttributeValue("style", "padding-top:10px;").first();
            String string = textField.text().split(": ")[1];
            return string.split(", ");
        } catch (Exception e) {
            throw new ParserException("Unable to parse HTML", e);
        }
    }

}
