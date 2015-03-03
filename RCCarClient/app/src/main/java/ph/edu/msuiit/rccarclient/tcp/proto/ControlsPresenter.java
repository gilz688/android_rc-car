package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsPresenter {
    public void onStart(Device device);

    public void onForwardButtonTouched();
    public void onForwardButtonReleased();

    public void onBackwardButtonTouched();
    public void onBackwardButtonReleased();

    public void onRightButtonTouched();
    public void onRightButtonReleased();

    public void onLeftButtonTouched();
    public void onLeftButtonReleased();
}