package de.jzbor.epos.elternportal;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class ElternPortal {
    private static ElternPortal ourInstance = new ElternPortal();
    private String school;
    private String user;
    private String pswd;

    private ElternPortal() {
    }

    public static ElternPortal getInstance() {
        return ourInstance;
    }

    private static String getHTML(String contentAddr, String user, String pswd, String school) throws Exception {
        // Define addresses for the request
        String basicAddr = "https://" + school + ".eltern-portal.org";
        String loginAddr = "https://eltern-portal.org/includes/project/auth/login.php";
        contentAddr = "https://" + school + ".eltern-portal.org/" + contentAddr;
        // Define post request params
        String requestProperties = "go_to=&username=" + user + "&password=" + pswd;
        byte[] postdata = requestProperties.getBytes(StandardCharsets.UTF_8);
        // Retrieve cookie from basic url
        String cookie;
        URL cookieUrl = new URL(basicAddr);
        HttpsURLConnection cookieconnection = (HttpsURLConnection) cookieUrl.openConnection();
        cookie = cookieconnection.getHeaderField("Set-Cookie").split(";")[0];
        // Perform login at login addr
        URL loginUrl = new URL(loginAddr);
        HttpsURLConnection loginConnection = (HttpsURLConnection) loginUrl.openConnection();
        loginConnection.setDoOutput(true);
        loginConnection.setInstanceFollowRedirects(false);
        loginConnection.setRequestMethod("POST");
        loginConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        loginConnection.setRequestProperty("Cookie", cookie);
        loginConnection.setRequestProperty("Content-Length", Integer.toString(postdata.length));
        OutputStreamWriter out = new OutputStreamWriter(loginConnection.getOutputStream());
        out.write(requestProperties);
        out.flush();
        out.close();
        // Request target content
        cookie = loginConnection.getHeaderField("Set-Cookie");
        URL contentUrl = new URL(contentAddr);
        HttpsURLConnection contentConnection = (HttpsURLConnection) contentUrl.openConnection();
        contentConnection.setRequestProperty("Cookie", cookie);
        // Read out response
        InputStreamReader inputStreamReader = new InputStreamReader(contentConnection.getInputStream());
        String str = "";
        int data = 0;
        while (data != -1) {
            data = inputStreamReader.read();
            str += (char) data;
        }
        inputStreamReader.close();
        return str;
    }

    public static boolean checkLogin(String user, String pswd, String school) throws Exception {
        // Check whether a certain login is valid
        String str = ElternPortal.getHTML("start", user, pswd, school);
        return str.contains("letzter Login");
    }

    public String getHTML(String contentAddr) throws Exception {
        // Request a certain content
        if (!loggedIn()) {
            throw new ImplicitLoginException();
        }
        return ElternPortal.getHTML(contentAddr, user, pswd, school);
    }

    public boolean loggedIn() {
        return ((user != null) && (pswd != null) && (school != null));
    }

    public synchronized void login(String user, String pswd, String school) {
        this.user = user;
        this.pswd = pswd;
        this.school = school;
    }
}
