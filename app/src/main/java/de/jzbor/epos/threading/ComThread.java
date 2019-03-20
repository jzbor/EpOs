package de.jzbor.epos.threading;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.jzbor.epos.elternportal.Dates;
import de.jzbor.epos.elternportal.ElternPortal;
import de.jzbor.epos.elternportal.ImplicitLoginException;
import de.jzbor.epos.elternportal.ParserException;
import de.jzbor.epos.elternportal.Schedule;
import de.jzbor.epos.elternportal.SubstitutePlanParser;

public class ComThread extends Thread {

    public static final String WEB_SUBDIR_SUBPLAN = "service/vertretungsplan";
    public static final String WEB_SUBDIR_SCHEDULE = "service/stundenplan";
    public static final String WEB_SUBDIR_DATES = "service/termine/liste";
    private static final String TAG = "ComThread";
    private ConnectivityManager connectivityManager;
    private UniHandler handler;
    private String request;

    public ComThread(ConnectivityManager cm, UniHandler h) {
        super();
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
        int returnCode = 0;
        String timestamp = "0";
        int responseType;
        Object returnObject = "";
        Message msg = handler.obtainMessage(UniHandler.ERROR_UNKNOWN);

        try {
            // Check for network connectivity
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if ((networkInfo == null) || (!networkInfo.isConnectedOrConnecting())) {
                responseType = UniHandler.ERROR_EP_CONNECTION;
            } else {
                ElternPortal ep = ElternPortal.getInstance();
                // Request html content
                String responseString = ep.getHTML(request);
                timestamp = new SimpleDateFormat("E HH:mm").format(new Date());
                // Switch for handling of different requests
                switch (request) {
                    case WEB_SUBDIR_SUBPLAN: {
                        responseType = UniHandler.EP_RESPONSE_SUBPLAN;
                        returnObject = SubstitutePlanParser.getSubstitutions(responseString);
                        break;
                    }
                    case WEB_SUBDIR_SCHEDULE: {
                        responseType = UniHandler.EP_RESPONSE_SCHEDULE;
                        Schedule sch = new Schedule(responseString);
                        sch.addClasses(responseString);
                        sch.filter();
                        returnObject = sch;
                        // Report name and class
                        Message ncMsg = handler.obtainMessage(UniHandler.EP_REPORT_NAME_CLASS, SubstitutePlanParser.parseNameClass(responseString));
                        ncMsg.sendToTarget();
                        break;
                    }
                    case WEB_SUBDIR_DATES: {
                        responseType = UniHandler.EP_RESPONSE_DATES;
                        returnObject = new Dates(responseString);
                        System.out.println(returnObject);
                        break;
                    }
                    case "personal": {
                        // @TODO implement
                        responseType = UniHandler.EP_RESPONSE_PERSONAL;
                        break;
                    }
                    default: {
                        // @TODO more detailed error
                        responseType = UniHandler.ERROR_UNKNOWN;
                    }
                }
            }
            // Pack return array
            Object[] objects = new Object[2];
            objects[0] = returnObject;
            objects[1] = timestamp;
            msg = handler.obtainMessage(responseType, objects);
            // Handle various exceptions
        } catch (IOException e) {
            e.printStackTrace();
            msg = handler.obtainMessage(UniHandler.ERROR_EP_CONNECTION);
        } catch (ImplicitLoginException e) {
            e.printStackTrace();
            msg = handler.obtainMessage(UniHandler.ERROR_EP_LOGIN);
        } catch (ParserException e) {
            e.printStackTrace();
            msg = handler.obtainMessage(UniHandler.ERROR_EP_PARSING);
        } catch (Exception e) {
            msg = handler.obtainMessage(UniHandler.ERROR_UNKNOWN);
            e.printStackTrace();
        } finally {
            msg.sendToTarget();
        }
    }
}
