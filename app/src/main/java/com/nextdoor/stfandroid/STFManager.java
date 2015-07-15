package com.nextdoor.stfandroid;

import android.content.Context;

/**
 * Manages STFSessions in the app.
 */
public class STFManager {
    private static STFSession stfSession;

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
}
