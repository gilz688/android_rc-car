package com.github.gilz688.rccarserver.proto;

public interface ServerInteractor {
    void stopRCServer();
    void enableRCService();
    void disableRCService();
    boolean isRCServiceEnabled();
}
