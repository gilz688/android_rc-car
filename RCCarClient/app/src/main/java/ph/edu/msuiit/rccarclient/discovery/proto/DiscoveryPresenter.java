package ph.edu.msuiit.rccarclient.discovery.proto;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.models.Device;

public interface DiscoveryPresenter {

    /*
     *  Refresh Button is clicked
     */
    public void onRefreshClicked();

    /*
     *  Action performed when Server Discovery is started
     */
    public void discoveryStarted();

    /*
     *  Action performed when an error during Server Discovery occurs
     */
    void discoveryError(String message);

    /*
     *  Action performed when Server Discovery has ended
     */
    public void discoveryEnded();

    /*
     *  Action performed when a device is found during Server Discovery
     */
    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort);

    /*
     *  Action performed when a Discovery View is displayed
     */
    public void onStart();

    /*
     *  Action performed when a Discovery View is closed
     */
    public void onEnd();

    /*
     *  Action performed when Wi-Fi Settings material_button is tapped
     */
    public void onClickWifiSettings();

    /*
     *  Action performed when a device on the list is tapped
     */
    public void onItemClicked(Device device);
}
