package ph.edu.msuiit.rccarclient.tcp;

import ph.edu.msuiit.rccarclient.common.RCResponse;
import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsInteractor;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsPresenter;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsView;

public class ControlsPresenterImpl implements ControlsPresenter {
    private ControlsInteractor mInteractor;
    private ControlsView mView;

    public ControlsPresenterImpl(ControlsView view, ControlsInteractor interactor) {
        mView = view;
        mInteractor = interactor;
    }

    @Override
    public void onStart(Device device) {
        mInteractor.startTCPConnection(device);
    }

    @Override
    public void onEnd() {
        mInteractor.stopTCPConnection();
    }



    @Override
    public void onVSeekBarProgressChanged(int value) {
        mInteractor.move(value);
    }

    @Override
    public void onHSeekBarProgressChanged(int value) {
        mInteractor.steer(value);
    }

    @Override
    public void onHornButtonTouched() {
        mInteractor.sendHornCommand();
    }

    @Override
    public void onHornButtonReleased() {
        mInteractor.sendStopHornCommand();
    }

    @Override
    public void tcpConnected() {

    }

    @Override
    public void tcpDisconnected() {
        mView.closeView();
    }

    @Override
    public void tcpResponseReceived(RCResponse response) {

    }
}
