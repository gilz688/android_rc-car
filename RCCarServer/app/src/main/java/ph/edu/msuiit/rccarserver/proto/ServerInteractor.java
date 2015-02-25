package ph.edu.msuiit.rccarserver.proto;

public interface ServerInteractor {
    public void stopRCServer();

    public void enableRCService();

    public void disableRCService();

    boolean isRCServiceEnabled();
}
