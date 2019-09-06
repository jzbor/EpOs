package de.jzbor.epos.data.dsb;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import de.jzbor.epos.data.DataHandler;
import de.jzbor.epos.data.DataProvider;
import de.jzbor.epos.data.Subplan;

public class DSBProvider extends Thread implements DataProvider {

    private static String user, pswd, filter;
    private DataHandler handler;
    private int id;

    public static synchronized void login(String user, String pswd) {
        DSBProvider.user = user;
        DSBProvider.pswd = pswd;
    }

    public static boolean checkLogin(String user, String pswd) throws IOException {
        // Check whether a certain login is valid
        String id = DSBNetwork.requestSubplanId(user, pswd);
        return !id.equals("00000000-0000-0000-0000-000000000000");
    }

    public static void setFilter(String filter) {
        DSBProvider.filter = filter;
    }

    public static boolean loggedIn() {
        return ((user != null) && (pswd != null));
    }

    @Override
    public synchronized void start() {
        System.err.println("DBBProvider.start() should not be called from a public context!");
    }

    @Override
    public void run() {
        try {
            if (!(loggedIn() && checkLogin(user, pswd))) {
                handler.handle(DataHandler.ERROR_LOGIN, id, null);
                return;
            }
            String subplanId = DSBNetwork.requestSubplanId(user, pswd);
            String subinfo = DSBNetwork.requestSubplans(subplanId);
            Map<String, String> m = DSBParser.parseSubplanInfo(subinfo);
            String requestUrl = m.get(DSBParser.SUBPLAN_URL_KEY).replaceAll("\\\\", "");
            String subplanHTML = DSBNetwork.request(requestUrl);
            System.out.println(subplanId + " - " + subinfo);
            Subplan subplan = DSBParser.parseSubplan(subplanHTML);
            handler.handle(DataHandler.RESPONSE_SUBPLAN, id, subplan);
        } catch (IOException e) {
            e.printStackTrace();
            handler.handle(DataHandler.ERROR_CONNECTION, id, null);
        }
    }

    @Override
    public int requestSubplan(DataHandler handler) {
        id = UUID.randomUUID().hashCode();
        this.handler = handler;
        super.start();
        return id;
    }

    @Override
    public int requestSchedule(DataHandler handler) {
        return 0;
    }

    @Override
    public int requestCalendar(DataHandler handler) {
        return 0;
    }

    @Override
    public int requestNotifications(DataHandler handler) {
        return 0;
    }

    @Override
    public boolean available() {
        return DSBProvider.loggedIn();
    }

    @Override
    public boolean providesSubplan() {
        return true;
    }

    @Override
    public boolean providesSchedule() {
        return false;
    }

    @Override
    public boolean providesCalendar() {
        return false;
    }

    @Override
    public boolean providesNotifications() {
        return false;
    }
}
