package com.as.fpphone.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

public class ProximitySensorManager {

    private final SensorManager sensorManager;
    private final Sensor proximitySensor;
    private final PowerManager.WakeLock wakeLock;

    private boolean isRegistered = false;

    @SuppressLint("InvalidWakeLockTag")
    public ProximitySensorManager(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "ProximitySensorWakeLock");
    }

    public void registerListener() {
        if (!isRegistered && proximitySensor != null) {
            sensorManager.registerListener(sensorEventListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
            isRegistered = true;
        }
    }

    public void unRegisterListener() {
        if (isRegistered) {
            sensorManager.unregisterListener(sensorEventListener);
            isRegistered = false;
            releaseWakeLock();
        }
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float distance = event.values[0];
                assert proximitySensor != null;
                if (distance < proximitySensor.getMaximumRange()) {
                    // Object is near and the screen should turn off
                    turnOffScreen();
                } else {
                    // Object is far from sensor and screen should turn on
                    turnOnScreen();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // Not used
        }
    };

    private void turnOffScreen() {
        //Turning the screen off when object is near
        acquireWakeLock();
    }

    private void turnOnScreen() {
        //Releasing the wakelock to turn on the screen
        releaseWakeLock();
    }

    @SuppressLint("WakelockTimeout")
    private void acquireWakeLock() {
        if (wakeLock != null && !wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();
        }
    }
}
