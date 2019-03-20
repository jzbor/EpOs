package de.jzbor.epos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class App {
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
}
