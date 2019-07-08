package de.jzbor.epos.threading;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;

import de.jzbor.epos.data.DataHandler;
import de.jzbor.epos.data.elternportal.Calendar;
import de.jzbor.epos.data.elternportal.ElternPortal;
import de.jzbor.epos.data.elternportal.ImplicitLoginException;
import de.jzbor.epos.data.elternportal.ParserException;
import de.jzbor.epos.data.elternportal.Schedule;
import de.jzbor.epos.data.elternportal.Subplan;
import de.jzbor.epos.data.elternportal.SubstitutePlanParser;

public class EPThread extends Thread {

    public static final String WEB_SUBDIR_SUBPLAN = "service/vertretungsplan";
    public static final String WEB_SUBDIR_SCHEDULE = "service/stundenplan";
    public static final String WEB_SUBDIR_DATES = "service/termine/liste";
    private static final String TAG = "EPThread";
    private ConnectivityManager connectivityManager;
    private DataHandler handler;
    private String request;
    private int id;

    public EPThread(ConnectivityManager cm, DataHandler h, int id) {
        super();
        this.id = id;
        connectivityManager = cm;
        handler = h;
    }

    public void start(String request) {
        this.request = request;
        super.start();
    }

    @Override
    public void run() {
        // Abort if request is missing
        if (request == null)
            return;

        // Initialize return elements
        int responseType;
        Object returnObject = "";

        try {
            // Check for network connectivity
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo == null) || (!networkInfo.isConnectedOrConnecting())) {
                responseType = UniHandler.ERROR_CONNECTION;
            } else {
                ElternPortal ep = ElternPortal.getInstance();
                // Request html content
                String responseString = ep.getHTML(request);
                // Switch for handling of different requests
                switch (request) {
                    case WEB_SUBDIR_SUBPLAN: {
                        responseType = UniHandler.RESPONSE_SUBPLAN;
                        returnObject = new Subplan(responseString);
                        break;
                    }
                    case WEB_SUBDIR_SCHEDULE: {
                        responseType = UniHandler.RESPONSE_SCHEDULE;
                        Schedule sch = new Schedule(responseString);
                        sch.addClasses(responseString);
                        sch.filter();
                        returnObject = sch;
                        // Report name and class
                        handler.handle(DataHandler.REPORT_NAME_CLASS, id,
                                SubstitutePlanParser.parseNameClass(responseString));
                        break;
                    }
                    case WEB_SUBDIR_DATES: {
                        responseType = UniHandler.RESPONSE_DATES;
                        returnObject = new Calendar(responseString);
                        System.out.println(returnObject);
                        break;
                    }
                    case "personal": {
                        // @TODO implement
                        responseType = UniHandler.RESPONSE_PERSONAL;
                        break;
                    }
                    default: {
                        // @TODO more detailed error
                        responseType = UniHandler.ERROR_UNKNOWN;
                    }
                }
            }
            // Pack return array
            handler.handle(responseType, id, returnObject);
            // Handle various exceptions
        } catch (IOException e) {
            e.printStackTrace();
            handler.handle(DataHandler.ERROR_CONNECTION, id, null);
        } catch (ImplicitLoginException e) {
            e.printStackTrace();
            handler.handle(DataHandler.ERROR_LOGIN, id, null);
        } catch (ParserException e) {
            e.printStackTrace();
            handler.handle(DataHandler.ERROR_PARSING, id, null);
        } catch (Exception e) {
            handler.handle(DataHandler.ERROR_UNKNOWN, id, null);
            e.printStackTrace();
        }
    }
}
