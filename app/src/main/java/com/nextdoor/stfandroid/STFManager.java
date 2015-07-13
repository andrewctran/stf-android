package com.nextdoor.stfandroid;

import android.app.AlertDialog;
import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

public class STFManager implements STFListener.ShakeListener {
    private static final String TAG = "STFManager";
    private SensorManager sensorManager;
    private STFListener stfListener;
    private Context context;

    public STFManager(Context context) {
        this.context = context;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        stfListener = new STFListener(this);
    }

    @Override
    public void onShake() {
        Log.d(TAG, "onShake()");
    }

    public void onResume() {
        stfListener.startListening(sensorManager);
    }

    public void onPause() {
        stfListener.stopListening();
    }
}