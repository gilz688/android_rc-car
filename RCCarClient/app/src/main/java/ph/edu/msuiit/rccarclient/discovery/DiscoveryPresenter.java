package ph.edu.msuiit.rccarclient.discovery;

import java.net.InetAddress;

public interface DiscoveryPresenter {

    public void onRefreshClicked();

    public void discoveryStarted();

    void discoveryError(String message);

    public void discoveryEnded();

    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort);

    public void onStart();

    public void onEnd();

    public void onClickWifiSettings();
}
