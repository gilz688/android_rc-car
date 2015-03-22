package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsPresenter {
    public void onStart(Device device);
    public void onEnd();

    public void onVSeekBarProgressChanged(int value);
    public void onHSeekBarProgressChanged(int value);

    public void onHornButtonTouched();
    public void onHornButtonReleased();
}