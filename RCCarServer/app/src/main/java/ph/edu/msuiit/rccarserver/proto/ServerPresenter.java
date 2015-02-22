package ph.edu.msuiit.rccarserver.proto;

public interface ServerPresenter {
    public void onClickStopListening();

    public void onClickStartListening();

    public void onStart();

    public void onStop();
}
