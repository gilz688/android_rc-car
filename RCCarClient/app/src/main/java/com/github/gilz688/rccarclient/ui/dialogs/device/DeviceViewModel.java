package com.github.gilz688.rccarclient.ui.dialogs.device;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.gilz688.rccarclient.discovery.DiscoveryBroadcastReceiver;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.util.Event;

import java.net.InetAddress;

public class DeviceViewModel extends ViewModel implements DiscoveryBroadcastReceiver.DiscoveryEventListener {
    public enum DeviceViewState {
        DISCOVERING,
        DISCOVERED,
        ERROR
    }

    private final MutableLiveData<Event<DeviceViewState>> stateData = new MutableLiveData<>();
    private final MutableLiveData<Event<Device>> discoveredDeviceData = new MutableLiveData<>();
    private final MutableLiveData<Event<String>> errorData = new MutableLiveData<>();

    public MutableLiveData<Event<DeviceViewState>> getStateData() {
        return stateData;
    }

    public MutableLiveData<Event<Device>> getDiscoveredDeviceData() {
        return discoveredDeviceData;
    }

    public MutableLiveData<Event<String>> getErrorData() {
        return errorData;
    }

    @Override
    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort) {
        Device device = new Device(serverName, serverAddress, serverPort);
        stateData.postValue(new Event<>(DeviceViewState.DISCOVERED));
        discoveredDeviceData.postValue(new Event<>(device));
    }

    @Override
    public void discoveryStarted() {
        stateData.postValue(new Event<>(DeviceViewState.DISCOVERING));
    }

    @Override
    public void discoveryError(String error) {
        errorData.postValue(new Event<>(error));
        stateData.postValue(new Event<>(DeviceViewState.ERROR));
    }

    @Override
    public void discoveryEnded() {
        if(stateData.getValue() == null) return;

        if(DeviceViewState.DISCOVERED == stateData.getValue().peekContent()) return;

        stateData.postValue(new Event<>(DeviceViewState.ERROR));
    }
}
