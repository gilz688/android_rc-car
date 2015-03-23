package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.common.RCResponse;
import ph.edu.msuiit.rccarclient.models.Device;

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