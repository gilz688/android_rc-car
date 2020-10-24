package com.github.gilz688.rccarclient.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.util.Event;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> connectedData = new MutableLiveData<>(false);
    private final MutableLiveData<Device> deviceData = new MutableLiveData<>();
    private final MutableLiveData<Integer> speedData = new MutableLiveData<>();
    private final MutableLiveData<Integer> angleData = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> hornData = new MutableLiveData<>();

    public void setDevice(Device device) {
        deviceData.postValue(device);
    }

    public void disconnectDevice() {
        deviceData.postValue(null);
        connectedData.postValue(false);
    }
    public void setSpeed(int speed) {
        speedData.postValue(speed);
    }

    public void setAngle(int angle) {
        angleData.postValue(angle);
    }

    public void stopHorn() {
        hornData.postValue(new Event<>(false));
    }

    public void startHorn() {
        hornData.postValue(new Event<>(true));
    }

    public MutableLiveData<Boolean> getConnectedData() {
        return connectedData;
    }

    public MutableLiveData<Device> getDeviceData() {
        return deviceData;
    }

    public MutableLiveData<Integer> getSpeedData() {
        return speedData;
    }

    public MutableLiveData<Integer> getAngleData() {
        return angleData;
    }

    public MutableLiveData<Event<Boolean>> getHornData() {
        return hornData;
    }
}
