package com.github.gilz688.rccarclient.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.api.RCCarService;
import com.github.gilz688.rccarclient.inject.RCCarComponent;
import com.github.gilz688.rccarclient.interact.SensorInteractor;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.model.RCCommand;
import com.github.gilz688.rccarclient.util.Event;
import com.github.gilz688.rccarclient.view.DeviceDialogFragment;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;

import okhttp3.OkHttpClient;

public class ControlsViewModel extends ViewModel implements DeviceDialogFragment.DeviceDialogListener {
    private RCCarService rcCarService;

    public enum Action {
        SHOW_CONNECT_DIALOG,
        SHOW_THEME_DIALOG,
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
    private final MutableLiveData<Boolean> connectedData = new MutableLiveData<>();
    private final MutableLiveData<Integer> fabConnectColorData = new MutableLiveData<>(R.color.gray);
    private final MutableLiveData<Boolean> enHorizontalJoystick = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> fabAccelerometerColor = new MutableLiveData<>(R.color.accent);
    private final MutableLiveData<Integer> verticalValue = new MutableLiveData<>();
    private final MutableLiveData<Integer> horizontalValue = new MutableLiveData<>();
    private LiveData<Integer> currentSensorReading;

    public void inject(RCCarComponent component) {
        sensorInteractor = new SensorInteractor(MINIMUM_ANGLE, MAXIMUM_ANGLE);
        component.inject(sensorInteractor);
        currentSensorReading = LiveDataReactiveStreams.fromPublisher(sensorInteractor.getLiveReading());
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
        sensorInteractor.enable();
        currentSensorReading = LiveDataReactiveStreams.fromPublisher(sensorInteractor.getLiveReading());
    }

    private void disableAccelerometer() {
        enHorizontalJoystick.postValue(false);
        sensorInteractor.disable();
    }

    public void onDeviceConnected() {
        fabConnectColorData.postValue(R.color.accent);
    }

    public void onDeviceDisconnected() {
        fabConnectColorData.postValue(R.color.gray);
    }

    public void vibrate() {
        actionData.setValue(new Event<>(Action.VIBRATE));
    }

    @Override
    public void onDeviceSelected(Device device) {
        deviceData.setValue(device);
    }
}
