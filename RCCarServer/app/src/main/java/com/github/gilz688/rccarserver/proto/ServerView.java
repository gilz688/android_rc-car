package com.github.gilz688.rccarserver.proto;

public interface ServerView {
    void showListeningView();
    void hideListeningView();
    void showAddress(String address);
    void showSSID(String ssid);
}
