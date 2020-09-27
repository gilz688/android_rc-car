package ph.edu.msuiit.rccarserver.proto;

public interface ServerInteractor {
    void stopRCServer();
    void enableRCService();
    void disableRCService();
    boolean isRCServiceEnabled();
}
