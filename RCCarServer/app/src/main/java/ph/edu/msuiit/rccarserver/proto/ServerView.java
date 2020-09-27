package ph.edu.msuiit.rccarserver.proto;

public interface ServerView {
    void showListeningView();
    void hideListeningView();
    void showAddress(String address);
    void showSSID(String ssid);
}
