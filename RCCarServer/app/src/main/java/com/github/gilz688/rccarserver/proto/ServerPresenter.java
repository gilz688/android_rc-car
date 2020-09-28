package com.github.gilz688.rccarserver.proto;

public interface ServerPresenter {
    void onClickStopListening();
    void onClickStartListening();
    void onStart();
    void onStop();
}
