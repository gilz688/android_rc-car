package com.github.gilz688.rccarserver.proto;

public interface ServerView {
    void showListeningView();
    void hideListeningView();
    void showAddress(String address);

    void showUdpPort(int tcpPort);

    void showTcpPort(int tcpPort);
}
