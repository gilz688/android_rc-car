package com.github.gilz688.rccarclient.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.api.RCCarService;
import com.github.gilz688.rccarclient.inject.RCCarComponent;
import com.github.gilz688.rccarclient.interact.SensorInteractor;
import com.github.gilz688.rccarclient.model.ControlsConfig;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.model.RCCommand;
import com.github.gilz688.rccarclient.util.ControlsPreferences;
import com.github.gilz688.rccarclient.util.Event;
import com.github.gilz688.rccarclient.view.DeviceDialogFragment;

public class ControlsViewModel extends ViewModel implements DeviceDialogFragment.DeviceDialogListener {
    private RCCarService rcCarService;
    private ControlsPreferences controlsPreferences;

    public enum Action {
        SHOW_CONNECT_DIALOG,
        SHOW_THEME_DIALOG,
        SHOW_CONTROL_SETTINGS_DIALOG,
        VIBRATE
    }

    // Constants
    public static final int MAXIMUM_SPEED = 3;
    public static final int MINIMUM_SPEED = -3;
    public static final int MAXIMUM_ANGLE = 3;
    public static final int MINIMUM_ANGLE = -3;

    // Allows us to obtain data from accelerometer and magnetometer
    private SensorInteractor sensorInteractor;

    // LiveData
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();
    private final MutableLiveData<Event<Action>> actionData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> connectedData = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> fabConnectColorData = new MutableLiveData<>(R.color.gray);
    private final MutableLiveData<Boolean> enHorizontalJoystick = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> fabAccelerometerColor = new MutableLiveData<>(R.color.accent);
    private final MutableLiveData<Integer> verticalValue = new MutableLiveData<>(0);
    private final MutableLiveData<Integer> horizontalValue = new MutableLiveData<>(0);
    private final MutableLiveData<ControlsConfig> controlsConfig = new MutableLiveData<>(new ControlsConfig());
    private LiveData<Integer> currentSensorReading;

    public void inject(RCCarComponent component) {
        sensorInteractor = new SensorInteractor(MINIMUM_ANGLE, MAXIMUM_ANGLE);
        component.inject(sensorInteractor);
        currentSensorReading = LiveDataReactiveStreams.fromPublisher(sensorInteractor.getLiveReading());
    }

    public void initPreferences(Context context) {
        if (controlsPreferences == null) {
            controlsPreferences = new ControlsPreferences(context);
            controlsConfig.setValue(controlsPreferences.loadConfig());
        }
    }

    public LiveData<ControlsConfig> getControlsConfig() {
        return controlsConfig;
    }

    public void setHandSwap(boolean handSwap) {
        ControlsConfig cur = controlsConfig.getValue();
        if (cur == null) {
            cur = new ControlsConfig();
        }
        if (cur.isHandSwap() != handSwap) {
            ControlsConfig updated = new ControlsConfig(
                    handSwap,
                    cur.getControlMode(),
                    cur.isInvertThrottle(),
                    cur.isInvertSteering()
            );
            controlsConfig.setValue(updated);
            if (controlsPreferences != null) {
                controlsPreferences.saveConfig(updated);
            }
            vibrate();
        }
    }

    public void setControlMode(ControlsConfig.ControlMode mode) {
        ControlsConfig cur = controlsConfig.getValue();
        if (cur == null) {
            cur = new ControlsConfig();
        }
        if (cur.getControlMode() != mode) {
            ControlsConfig updated = new ControlsConfig(
                    cur.isHandSwap(),
                    mode,
                    cur.isInvertThrottle(),
                    cur.isInvertSteering()
            );
            controlsConfig.setValue(updated);
            if (controlsPreferences != null) {
                controlsPreferences.saveConfig(updated);
            }
            if (mode == ControlsConfig.ControlMode.ACCELEROMETER) {
                enableAccelerometer();
            } else {
                disableAccelerometer();
            }
            vibrate();
        }
    }

    public void setInvertThrottle(boolean invert) {
        ControlsConfig cur = controlsConfig.getValue();
        if (cur == null) {
            cur = new ControlsConfig();
        }
        if (cur.isInvertThrottle() != invert) {
            ControlsConfig updated = new ControlsConfig(
                    cur.isHandSwap(),
                    cur.getControlMode(),
                    invert,
                    cur.isInvertSteering()
            );
            controlsConfig.setValue(updated);
            if (controlsPreferences != null) {
                controlsPreferences.saveConfig(updated);
            }
            vibrate();
        }
    }

    public void setInvertSteering(boolean invert) {
        ControlsConfig cur = controlsConfig.getValue();
        if (cur == null) {
            cur = new ControlsConfig();
        }
        if (cur.isInvertSteering() != invert) {
            ControlsConfig updated = new ControlsConfig(
                    cur.isHandSwap(),
                    cur.getControlMode(),
                    cur.isInvertThrottle(),
                    invert
            );
            controlsConfig.setValue(updated);
            if (controlsPreferences != null) {
                controlsPreferences.saveConfig(updated);
            }
            vibrate();
        }
    }

    public void onThrottleInput(int value) {
        ControlsConfig config = controlsConfig.getValue();
        int finalValue = (config != null && config.isInvertThrottle()) ? -value : value;
        verticalValue.setValue(clamp(finalValue, MINIMUM_SPEED, MAXIMUM_SPEED));
    }

    public void onSteeringInput(int value) {
        ControlsConfig config = controlsConfig.getValue();
        int finalValue = (config != null && config.isInvertSteering()) ? -value : value;
        horizontalValue.setValue(clamp(finalValue, MINIMUM_ANGLE, MAXIMUM_ANGLE));
    }

    private int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public MutableLiveData<Event<Action>> getActionData() {
        return actionData;
    }

    public MutableLiveData<Device> getDeviceData() {
        return deviceData;
    }

    public MutableLiveData<Boolean> getConnectedData() {
        return connectedData;
    }

    public LiveData<Integer> getCurrentSensorReading() {
        return currentSensorReading;
    }

    public MutableLiveData<Integer> getFabConnectColorData() {
        return fabConnectColorData;
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

    public void onClickThemeButton() {
        vibrate();
        actionData.setValue(new Event<>(Action.SHOW_THEME_DIALOG));
    }

    public void onClickSettingsButton() {
        vibrate();
        actionData.setValue(new Event<>(Action.SHOW_CONTROL_SETTINGS_DIALOG));
    }

    public void onClickConnectButton() {
        vibrate();
        actionData.setValue(new Event<>(Action.SHOW_CONNECT_DIALOG));
    }

    public void onPressHornButton() {
        vibrate();
    }

    public void onReleaseHornButton() {
        vibrate();
    }

    public void onClickAccelerometerButton() {
        if (enHorizontalJoystick.getValue() == null) return;

        boolean enAccelerometerValue = enHorizontalJoystick.getValue();
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
        enHorizontalJoystick.postValue(true);
        if (sensorInteractor != null) {
            sensorInteractor.enable();
            currentSensorReading = LiveDataReactiveStreams.fromPublisher(sensorInteractor.getLiveReading());
        }
    }

    private void disableAccelerometer() {
        enHorizontalJoystick.postValue(false);
        if (sensorInteractor != null) {
            sensorInteractor.disable();
        }
    }

    public void onDeviceConnected() {
        connectedData.postValue(true);
        fabConnectColorData.postValue(R.color.accent);
    }

    public void onDeviceDisconnected() {
        connectedData.postValue(false);
        fabConnectColorData.postValue(R.color.gray);
    }

    public void vibrate() {
        actionData.setValue(new Event<>(Action.VIBRATE));
    }

    @Override
    public void onDeviceSelected(Device device) {
        deviceData.setValue(device);
        onDeviceConnected();
    }
}
