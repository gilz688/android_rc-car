package com.github.gilz688.rccarclient.models;

public interface RCClient{
    void startClient();
    void stopClient();
    void sendCommand(String command, int value);
    void sendCommand(String command);
    boolean isRunning();
}
