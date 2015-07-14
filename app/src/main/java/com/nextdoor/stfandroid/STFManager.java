package com.nextdoor.stfandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.SensorManager;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class STFManager implements STFListener.ShakeListener {
    public static final String TAG = "STFManager";
    private SensorManager sensorManager;
    private STFListener stfListener;
    private Context context;
    private String imagePath;

    public STFManager(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stfListener = new STFListener(this);
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

    private void startAnnotateActivity() {
        Intent launchIntent = new Intent(context, STFAnnotateActivity.class);
        launchIntent.putExtra(TAG, imagePath);
        context.startActivity(launchIntent);

    }
}