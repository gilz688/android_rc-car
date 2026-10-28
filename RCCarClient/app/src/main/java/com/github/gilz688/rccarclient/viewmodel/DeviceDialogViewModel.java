package com.github.gilz688.rccarclient.viewmodel;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.model.DiscoveryClient;
import com.github.gilz688.rccarclient.api.RCCarService;
import com.github.gilz688.rccarclient.util.Event;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class DeviceDialogViewModel extends ViewModel implements DiscoveryClient.DiscoveryClientListener {
    public enum DeviceViewState {
        DISCOVERING,
        DISCOVERED,
        ERROR
    }

    @Inject WifiManager wifiManager;

    private final ExecutorService networkThread = Executors.newSingleThreadExecutor();
    private final MutableLiveData<Event<DeviceViewState>> stateData = new MutableLiveData<>();
    private final MutableLiveData<Event<Device>> discoveredDeviceData = new MutableLiveData<>();

    public MutableLiveData<Event<DeviceViewState>> getStateData() {
        return stateData;
    }
    public MutableLiveData<Event<Device>> getDiscoveredDeviceData() {
        return discoveredDeviceData;
    }

    public void startDiscovery() {
        networkThread.submit(new Thread(() -> {
            try {
            // Do network action in this function
            DiscoveryClient client = new DiscoveryClient(DiscoveryClient.DEFAULT_PORT, getBroadcastAddress());
            client.setOnServerFoundListener(DeviceDialogViewModel.this);
            client.startDiscovery();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }));
    }

    @Override
    public void onDiscoveryStarted() {
        stateData.postValue(new Event<>(DeviceViewState.DISCOVERING));
    }

    @Override
    public void onServerFound(String serverName, InetAddress serverAddress, int serverPort) {
        Device device = new Device(serverName, serverAddress, serverPort);
        stateData.postValue(new Event<>(DeviceViewState.DISCOVERED));
        discoveredDeviceData.postValue(new Event<>(device));
    }

    @Override
    public void onDiscoveryFailed() {
        if (stateData.getValue() == null) return;

        stateData.postValue(new Event<>(DeviceViewState.ERROR));
    }

    /**
     * Calculate the broadcast IP we need to send the packet along. If we send it
     * to 255.255.255.255, it never gets sent because some routers will block it by default.
     */
    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if (dhcp == null) {
            Log.d("DeviceViewModel", "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    @Override
    protected void onCleared() {
        // Don't forget to stop the running thread, if any
        networkThread.shutdown();
        super.onCleared();
    }
}
