package de.jzbor.epos;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;

public class App {
    public static final String TAG = "EpOs";

    public static void saveObject(File dir, String filename, Object object) throws IOException {
        // Save a serializable object
        if (!(object instanceof Serializable))
            return;
        File file = new File(dir, filename);
        FileOutputStream fos = new FileOutputStream(file);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(object);
        oos.close();
        fos.close();
    }

    public static Object openObject(File dir, String filename) throws IOException, ClassNotFoundException {
        // Open a serializable object
        File file = new File(dir, filename);
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        Object object = ois.readObject();
        ois.close();
        fis.close();
        return object;
    }

    public static void saveText(File dir, String filename, String text) throws IOException {
        // Save a string as plain text (.txt)
        File file = new File(dir, filename);
        FileOutputStream fos = new FileOutputStream(file);
        PrintWriter pw = new PrintWriter(fos);
        pw.write(text);
        pw.close();
        fos.close();
    }

    public static String openText(File dir, String filename) throws IOException {
        // Open a plain text file as string
        File file = new File(dir, filename);
        FileInputStream fis = new FileInputStream(file);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        int i;
        StringBuilder sb = new StringBuilder();
        while ((i = br.read()) != -1) {
            sb.append((char) i);
        }
        return sb.toString();
    }

    public static boolean inetReady(ConnectivityManager connectivityManager) {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return !((networkInfo == null) || (!networkInfo.isConnectedOrConnecting()));
    }
}
