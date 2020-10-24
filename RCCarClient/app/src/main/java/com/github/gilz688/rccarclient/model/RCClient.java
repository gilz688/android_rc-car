package com.github.gilz688.rccarclient.model;

public interface RCClient {
    void startClient();
    void stopClient();
    void sendCommand(String command, int value);
    void sendCommand(String command);
    boolean isRunning();
    void setEventListener(TCPClient.EventListener listener);
}
