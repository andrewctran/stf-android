package com.nextdoor.stfandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * A shake detector that lives implicitly in all app activities.
 */
public class STFSession implements STFListener.STFDetector {
    public static final String TAG = "STFSession";
    public static String imagePath;
    private SensorManager sensorManager;
    private STFListener stfListener;
    private Context context;
//    private String imagePath;
    private STFRequestThread stfRequestThread;

    public STFSession(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stfListener = new STFListener(this);
        stfRequestThread = new STFRequestThread(STFManager.getQueue());
        stfRequestThread.start();
    }

    @Override
    public void onShake() {
        View rootView = ((Activity) context).getWindow().getDecorView();
        Bitmap screenshot = STFAnnotator.takeScreenshot(rootView);
        STFAnnotator.saveScreenshot(screenshot);
        imagePath = context.getFilesDir().getPath();
        startAnnotateActivity();
    }

    public void onResume() {
        stfListener.startListening(sensorManager);
        stfRequestThread = new STFRequestThread(STFManager.getQueue());
    }

    public void onPause() {
        stfListener.stopListening();
        stfRequestThread.requestStop();
    }

    /**
     * Starts STFAnnotateActivity with the most recently grabbed screenshot on disk.
     */
    private void startAnnotateActivity() {
        Intent launchIntent = new Intent(context, STFAnnotateActivity.class);
        launchIntent.putExtra(TAG, imagePath);
        context.startActivity(launchIntent);
    }

    public STFRequestThread getRequestThread() {
        return stfRequestThread;
    }

    public void setQueue(List<STFItem> stfQueue) {
        this.stfRequestThread.setQueue(stfQueue);
    }
}