package com.nextdoor.library;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;

/**
 * Detects device shakes using most recently observed samples of accelerometer data.
 *
 * @author Andrew Tran (atran@nextdoor.com)
 */
public class STFListener implements SensorEventListener {
    /** An acceleration threshold used to classify shakes. Customizable.*/
    private static final int SHAKE_THRESHOLD = 2;

    private Sensor accelerometer;
    private SensorManager sensorManager;
    private AccelerationLog accelerationLog;
    private STFDetector stfDetector;

    public interface STFDetector {
        void onShake();
    }

    public STFListener(STFDetector stfDetector) {
        this.stfDetector = stfDetector;
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
            stfDetector.onShake();
            // Prevent detection of sequential shakes
            stopListening();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    startListening(sensorManager);
                }
            }, 1000);

        }
    }

    /**
     * Filters out acceleration events that do not meet the threshold. The square of the magnitude
     * of the acceleration vector is used to avoid expensive square root operation.
     *
     * @param event Android accelerometer event
     * @return true if the device is accelerating above the client-defined threshold
     */
    private boolean isAccelerating(SensorEvent event) {
        float gx = event.values[0] / SensorManager.GRAVITY_EARTH;
        float gy = event.values[1] / SensorManager.GRAVITY_EARTH;
        float gz = event.values[2] / SensorManager.GRAVITY_EARTH;
        double squareMagnitude = gx * gx + gy * gy + gz * gz;
        return squareMagnitude > SHAKE_THRESHOLD * SHAKE_THRESHOLD;
    }

    /**
     * Registers the accelerometer to start listening for shake events.
     * @param sensorManager
     * @return true if successful
     */
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

    /**
     * Unregisters the accelerometer.
     */
    public void stopListening() {
        if (accelerometer != null) {
            sensorManager.unregisterListener(this, accelerometer);
            accelerometer = null;
        }
    }

    /**
     * A running log of most recently reporter accelerometer data.
     */
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

        /**
         * Checks if the range of samples in the log were generated over a period of time longer than
         * the minimum threshold that is required for acceleration to be considered a shake.
         *
         * @return true if there are enough samples to query
         */
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

        /**
         * Clear the log.
         */
        void clear() {
            while (oldest != null) {
                AccelerationSample removed = oldest;
                oldest = oldest.next;
                newest = null;
                numSamples = 0;
                numAccelerating = 0;
            }
        }

        /**
         * Flushes stale data from the log relative to a cutoff time.
         *
         * @param cutoff Timestamp for the oldest piece of data to be retained.
         */
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

    /**
     * Timestamp-Boolean binding for logging.
     */
    class AccelerationSample {
        long timestamp;
        boolean isAccelerating;
        AccelerationSample next;
    }
}
