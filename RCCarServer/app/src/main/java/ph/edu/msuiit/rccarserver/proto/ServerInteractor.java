package ph.edu.msuiit.rccarserver.proto;

public interface ServerInteractor {
    public void startDiscoveryServer();

    public void stopDiscoveryServer();

    public void startTCPServer();

    public void stopTCPServer();
}
