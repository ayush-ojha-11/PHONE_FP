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
import android.widget.Toast;

public class ProximitySensorManager {

    private final Context context;
    private final SensorManager sensorManager;
    private final Sensor proximitySensor;
    private final Window window;
    private final PowerManager.WakeLock wakeLock;

    private boolean isRegistered = false;

    @SuppressLint("InvalidWakeLockTag")
    public ProximitySensorManager(Context context, Window window) {
        this.context = context;
        this.window = window;
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
        acquireWakeLock();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        }
    }

    private void turnOnScreen() {
        releaseWakeLock();
    }

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
