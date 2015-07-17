package com.nextdoor.stfandroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Environment;

/**
 * A shake detector that lives implicitly in all app activities.
 */
public class STFSession implements STFListener.STFDetector {
    public static final String TAG = "STFSession";
    private SensorManager sensorManager;
    private STFListener stfListener;
    private Context context;
    private String imagePath;
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
        Bitmap screenshot = STFAnnotator.takeScreenshot(((Activity) context).findViewById(android.R.id.content).getRootView());
        STFAnnotator.saveScreenshot(screenshot);
        imagePath = Environment.getExternalStorageDirectory() + "/STFScreenshot";
        startAnnotateActivity();
    }

    public void onResume() {
        stfListener.startListening(sensorManager);
    }

    public void onPause() {
        stfListener.stopListening();
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
}