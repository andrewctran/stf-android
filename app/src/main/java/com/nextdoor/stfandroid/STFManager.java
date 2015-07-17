package com.nextdoor.stfandroid;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Manages STFSessions in the app.
 */
public class STFManager {
    private static STFSession stfSession;
    private static List<STFItem> stfQueue;

    /**
     * Lazy loads STFSession
     * @param context
     * @return Valid STFSession
     */
    public static STFSession getInstance(Context context) {
        if (stfSession == null) {
            stfSession = new STFSession(context);
        }
        Timer timer = new Timer();
        timer.schedule(new SayHello(), 0, 5000);
        return stfSession;
    }

    public static List<STFItem> getQueue() {
        if (stfQueue == null) {
            ObjectInputStream inputStream;
            try {
                inputStream = new ObjectInputStream(new FileInputStream(new File(Environment.getExternalStorageDirectory(), "STFQueue")));
                stfQueue = (List<STFItem>) inputStream.readObject();
                inputStream.close();
            } catch (Exception e) {
            }
            if (stfQueue == null) {
                stfQueue = new ArrayList<STFItem>();
            }
        }
        return stfQueue;
    }

    public static void enqueue(STFItem stfItem) {
        stfQueue.add(stfItem);
        persist();
    }

    public static void dequeue() {
        stfQueue.remove(0);
    }

    public static void updateQueue() {
        stfSession.setQueue(stfQueue);
    }

    public static void persist() {
        ObjectOutputStream outputStream;
        try {
            File outFile = new File(Environment.getExternalStorageDirectory(), "STFQueue");
            outputStream = new ObjectOutputStream(new FileOutputStream(outFile));
            outputStream.writeObject(stfQueue);
            outputStream.flush();
            outputStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    private static class SayHello extends TimerTask {
        public void run() {
            System.out.println("Queue size: " + stfQueue.size());
        }
    }
}
