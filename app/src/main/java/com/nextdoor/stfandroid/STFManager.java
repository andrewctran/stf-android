package com.nextdoor.stfandroid;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

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
                return stfSession;
            }

            public static List<STFItem> getQueue() {
                if (stfQueue == null) {
                    ObjectInputStream inputStream;
                    try {
                        inputStream = new ObjectInputStream(new FileInputStream(STFConfig.PERSISTENT_STORAGE_PATH));
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
            ObjectOutputStream outputStream;
            try {
                outputStream = new ObjectOutputStream(new FileOutputStream(STFConfig.PERSISTENT_STORAGE_PATH));
                outputStream.writeObject(stfQueue);
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }
}
