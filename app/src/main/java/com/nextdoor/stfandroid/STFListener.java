package com.nextdoor.stfandroid;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.concurrent.ExecutionException;

public class STFListener implements SensorEventListener {
    private static final int SHAKE_THRESHOLD = 2;

    private Sensor accelerometer;
    private SensorManager sensorManager;
    private AccelerationLog accelerationLog;
    private ShakeListener shakeListener;

    public interface ShakeListener {
        void onShake();
    }

    public STFListener(ShakeListener shakelistener) {
        this.shakeListener = shakelistener;
        this.accelerationLog = new AccelerationLog();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do nothing
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long timestamp = event.timestamp;
        boolean isAccelerating = isAccelerating(event);
        accelerationLog.add(timestamp, isAccelerating);
        if (accelerationLog.isShaking()) {
            accelerationLog.clear();
            shakeListener.onShake();
        }
    }

    private boolean isAccelerating(SensorEvent event) {
        float gx = event.values[0] / SensorManager.GRAVITY_EARTH;
        float gy = event.values[1] / SensorManager.GRAVITY_EARTH;
        float gz = event.values[2] / SensorManager.GRAVITY_EARTH;
        double squareMagnitude = gx * gx + gy * gy + gz * gz;
        return squareMagnitude > SHAKE_THRESHOLD * SHAKE_THRESHOLD;
    }

    public boolean startListening(SensorManager sensorManager) {
        if (accelerometer != null) {
            return true;
        }
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            this.sensorManager = sensorManager;
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        }
        return accelerometer != null;
    }

    public void stopListening() {
        if (accelerometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            sensorManager = null;
            accelerometer = null;
        }
    }

    class AccelerationLog {
        private static final int MIN_SAMPLE_SIZE = 4;
        private static final long MAX_WINDOW_SIZE = 500000000;
        private static final long MIN_WINDOW_SIZE = 250000000;

        private int numSamples;
        private int numAccelerating;
        private AccelerationSample oldest;
        private AccelerationSample newest;

        boolean isShaking() {
            return hasEnoughSamples() && ((double) numAccelerating / (double) numSamples >= 0.8);
        }

        boolean hasEnoughSamples() {
            return newest != null && oldest != null && newest.timestamp - oldest.timestamp >= MIN_WINDOW_SIZE;
        }

        void add(long timestamp, boolean isAccelerating) {
            update(timestamp - MAX_WINDOW_SIZE);
            AccelerationSample sample = new AccelerationSample();
            sample.timestamp = timestamp;
            sample.isAccelerating = isAccelerating;
            sample.next = null;
            if (newest != null) {
                newest.next = sample;
            }
            if (oldest == null) {
                oldest = sample;
            }
            newest = sample;
            if (isAccelerating) {
                numAccelerating++;
            }
            numSamples++;
        }

        void clear() {
            while (oldest != null) {
                AccelerationSample removed = oldest;
                oldest = oldest.next;
                newest = null;
                numSamples = 0;
                numAccelerating = 0;
            }
        }

        void update(long cutoff) {
            while (numSamples > MIN_SAMPLE_SIZE && oldest != null && cutoff - oldest.timestamp > 0) {
                AccelerationSample removed = oldest;
                if (removed.isAccelerating) {
                    numAccelerating--;
                }
                numSamples--;
                oldest = removed.next;
                if (oldest == null) {
                    newest = null;
                }
            }
        }

    }

    class AccelerationSample {
        long timestamp;
        boolean isAccelerating;
        AccelerationSample next;
    }
}
