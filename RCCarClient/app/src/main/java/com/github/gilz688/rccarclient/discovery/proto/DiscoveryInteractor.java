package com.github.gilz688.rccarclient.discovery.proto;

import com.github.gilz688.rccarclient.models.Device;

/**
 *  Implements startDiscovery and connectToDevice use cases
 */
public interface DiscoveryInteractor {
    public void startDiscovery();

    public void connectToServer(Device device);

    public void openWifiSettings();
}
