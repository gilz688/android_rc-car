package com.github.gilz688.rccarclient.ui.fragment.main;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.util.Event;

public class MainViewModel extends ViewModel implements SensorEventListener {
    // Constants
    public static final int MAXIMUM_SPEED = 3;
    public static final int MINIMUM_SPEED = -3;
    public static final int MAXIMUM_ANGLE = 3;
    public static final int MINIMUM_ANGLE = -3;

    // Observables
    private final MutableLiveData<Boolean> vibrate = new MutableLiveData<>();
    private final MutableLiveData<Integer> fabConnectColor = new MutableLiveData<>(R.color.gray);
    private final MutableLiveData<Boolean> reqConnect = new MutableLiveData<>();
    private final MutableLiveData<Boolean> reqAppTheme = new MutableLiveData<>();
    private final MutableLiveData<Boolean> enHorizontalJoystick = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> fabAccelerometerColor = new MutableLiveData<>(R.color.accent);
    private final MutableLiveData<Integer> verticalValue = new MutableLiveData<>();
    private final MutableLiveData<Integer> horizontalValue = new MutableLiveData<>();

    // Allows us to obtain data from accelerometer and magnetometer
    private SensorManager mSensorManager;

    // Gravity rotational data
    float[] matrixR = null;
    // Magnetic rotational data
    float[] matrixI = null;
    // Accelerometer data
    float[] accelerometerValues = new float[3];
    // Magnetometer data
    float[] magnetometerValues = new float[3];
    // Angles for each axis
    float[] angle = new float[3];
    // Current filtered angle
    int currentAngle = 0;
    // Previous angle posted to UI
    int previousAngle = 0;
    // Smoothing constant for low-pass filer
    final float ALPHA = 0.5f;

    public void setSensorManager(SensorManager mSensorManager) {
        this.mSensorManager = mSensorManager;
    }

    public MutableLiveData<Boolean> getReqConnect() {
        return reqConnect;
    }

    public MutableLiveData<Integer> getFabConnectColor() {
        return fabConnectColor;
    }

    public MutableLiveData<Boolean> getEnHorizontalJoystick() {
        return enHorizontalJoystick;
    }

    public MutableLiveData<Integer> getVerticalValue() {
        return verticalValue;
    }

    public MutableLiveData<Integer> getHorizontalValue() {
        return horizontalValue;
    }

    public LiveData<Integer> getFabAccelerometerColor() {
        return fabAccelerometerColor;
    }

    public MutableLiveData<Boolean> getReqAppTheme() {
        return reqAppTheme;
    }

    public MutableLiveData<Boolean> getVibrate() {
        return vibrate;
    }

    public void onClickThemeButton() {
        reqAppTheme.postValue(true);
        vibrate();
    }

    public void onClickConnectButton() {
        reqConnect.postValue(true);
        vibrate();
    }

    public void onClickHornButton() {
        vibrate();
    }

    public void onClickAccelerometerButton() {
        if (enHorizontalJoystick.getValue() == null) return;

        boolean enAccelerometerValue = enHorizontalJoystick.getValue();

        enHorizontalJoystick.postValue(!enAccelerometerValue);
        if (enAccelerometerValue) {
            disableAccelerometer();
            fabAccelerometerColor.postValue(R.color.accent);
        } else {
            enableAccelerometer();
            fabAccelerometerColor.postValue(R.color.accent_variant);
        }

        vibrate();
    }

    private void enableAccelerometer() {
        Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void disableAccelerometer() {
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

            currentAngle = map(pitch, -20, 20, MINIMUM_ANGLE, MAXIMUM_ANGLE);
            Log.d("MainViewModel", "currentAngle: " + currentAngle);
            if(previousAngle != currentAngle) {
                previousAngle = currentAngle;
                horizontalValue.postValue(currentAngle);
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

    public void onDeviceConnected() {
        fabConnectColor.postValue(R.color.accent);
    }

    public void onDeviceDisconnected() {
        fabConnectColor.postValue(R.color.gray);
    }

    public void vibrate() {
        vibrate.postValue(true);
    }
}
