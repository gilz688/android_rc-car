package com.github.gilz688.rccarclient.interact;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class SensorInteractor implements SensorEventListener {
    // Allows us to obtain sensor data in Android
    @Inject SensorManager mSensorManager;
    private final int min;
    private final int max;

    // Gravity rotational data
    private float[] matrixR = null;
    // Magnetic rotational data
    private float[] matrixI = null;
    // Accelerometer data
    private float[] accelerometerValues = new float[3];
    // Magnetometer data
    private float[] magnetometerValues = new float[3];
    // Angles for each axis
    private float[] angle = new float[3];
    // Current filtered angle
    private int current = 0;
    // Previous angle posted to UI
    private int previous = 0;
    // Smoothing constant for low-pass filter
    private final float ALPHA = 0.5f;

    private final PublishSubject<Integer> readingUpdateSubject = PublishSubject.create();
    private final Flowable<Integer> readingUpdateFlowable = readingUpdateSubject.toFlowable(BackpressureStrategy.DROP);

    public SensorInteractor(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public Flowable<Integer> getLiveReading() {
        return readingUpdateFlowable;
    }

    public void enable() {
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disable() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerValues = lowPass(event.values.clone(), magnetometerValues);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = lowPass(event.values.clone(), magnetometerValues);
                break;
        }

        if (magnetometerValues != null && accelerometerValues != null) {
            matrixR = new float[9];
            matrixI = new float[9];

            SensorManager.getRotationMatrix(matrixR, matrixI, accelerometerValues, magnetometerValues);

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X, SensorManager.AXIS_MINUS_Y, outR);
            SensorManager.getOrientation(outR, angle);

            float pitch = angle[1] * 57.2957795f;

            magnetometerValues = null; // retrigger the loop when things are repopulated
            accelerometerValues = null; // retrigger the loop when things are repopulated

            current = map(pitch, -20, 20, min, max);
            Log.d("MainViewModel", "currentAngle: " + current);
            if (previous != current) {
                previous = current;
                readingUpdateSubject.onNext(current);
            }
        }
    }

    public int map(float value, float in_min, float in_max, float out_min, float out_max) {
        return Math.round((value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

    protected float[] lowPass(float[] input, float[] output) {
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }
}
