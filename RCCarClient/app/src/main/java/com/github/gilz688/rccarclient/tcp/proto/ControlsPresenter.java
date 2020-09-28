package com.github.gilz688.rccarclient.tcp.proto;

import com.github.gilz688.rccarclient.common.RCResponse;
import com.github.gilz688.rccarclient.models.Device;

public interface ControlsPresenter {
    public void onStart(Device device);
    public void onEnd();

    public void onVSeekBarProgressChanged(int value);
    public void onHSeekBarProgressChanged(int value);

    public void onHornButtonTouched();
    public void onHornButtonReleased();

    public void tcpConnected();
    public void tcpDisconnected();


    public void tcpResponseReceived(RCResponse response);
}