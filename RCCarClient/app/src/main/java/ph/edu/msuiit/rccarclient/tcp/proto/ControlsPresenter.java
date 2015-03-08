package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsPresenter {
    public void onStart(Device device);
    public void onEnd();

    public void onForwardButtonTouched();
    public void onForwardButtonReleased();

    public void onBackwardButtonTouched();
    public void onBackwardButtonReleased();

    public void onSeekBarProgressChangedRight(int value);
    public void onSeekBarProgressChangedLeft(int value);
    public void onSeekBarCentered();
}