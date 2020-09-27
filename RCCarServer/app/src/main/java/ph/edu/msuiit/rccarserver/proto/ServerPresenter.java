package ph.edu.msuiit.rccarserver.proto;

public interface ServerPresenter {
    void onClickStopListening();
    void onClickStartListening();
    void onStart();
    void onStop();
}
