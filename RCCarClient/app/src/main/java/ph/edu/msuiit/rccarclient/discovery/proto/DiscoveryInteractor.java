package ph.edu.msuiit.rccarclient.discovery.proto;

import ph.edu.msuiit.rccarclient.models.Device;

/**
 *  Implements startDiscovery and connectToServer use cases
 */
public interface DiscoveryInteractor {
    public void startDiscovery();

    public void connectToServer(Device device);

    public void openWifiSettings();
}
