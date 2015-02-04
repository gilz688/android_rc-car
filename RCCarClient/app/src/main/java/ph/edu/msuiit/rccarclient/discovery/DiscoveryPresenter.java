package ph.edu.msuiit.rccarclient.discovery;

import java.net.InetAddress;

public interface DiscoveryPresenter {

    public void onRefreshButtonClicked();

    public void discoveryStarted();

    public void discoveryError();

    public void discoveryEnded();

    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort);

    public void onStart();

    public void onEnd();
}
