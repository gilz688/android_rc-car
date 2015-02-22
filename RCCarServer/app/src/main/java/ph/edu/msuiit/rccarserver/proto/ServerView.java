package ph.edu.msuiit.rccarserver.proto;

public interface ServerView {
    public void showListeningView();
    public void hideListeningView();
    public void showAddress(String address);
    public void showSSID(String ssid);
}
