package de.jzbor.epos.data.dsb;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DSBNetwork {

    private static final String API_URL = "https://iphone.dsbcontrol.de/iPhoneService.svc/DSB";
    private static final String SUBPLAN_ID_SUBDIR = "/authid/%s/%s";
    private static final String SUBPLAN_SUBDIR = "/timetables/%s";

    public static String request(String addr) throws IOException {
        StringBuilder response = new StringBuilder();
        URL url = new URL(addr);
        System.out.println("Requesting: " + url.toString());
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        InputStreamReader isr = new InputStreamReader(connection.getInputStream());

        int data = 0;
        while (data != -1) {
            data = isr.read();
            if (data > 0)
                response.append((char) data);
        }
        return response.toString();
    }

    public static String requestSubplanId(String user, String pswd) throws IOException {
        return request(API_URL + String.format(SUBPLAN_ID_SUBDIR, user, pswd)).replaceAll("\"", "");
    }

    public static String requestSubplans(String id) throws IOException {
        return request(API_URL + String.format(SUBPLAN_SUBDIR, id));
    }

}
