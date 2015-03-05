package ph.edu.msuiit.rccarclient.tcp;

import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsInteractor;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsPresenter;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsView;

public class ControlsPresenterImpl implements ControlsPresenter{
    private ControlsInteractor mInteractor;
    private ControlsView mView;

    public ControlsPresenterImpl(ControlsView view, ControlsInteractor interactor) {
        mView = view;
        mInteractor = interactor;
    }

    @Override
    public void onStart(Device device) {
        mInteractor.establishTCPConnection(device);
    }

    @Override
    public void onForwardButtonTouched() {
        mInteractor.steerForward();
    }

    @Override
    public void onForwardButtonReleased() {
        mInteractor.stop();
    }

    @Override
    public void onBackwardButtonTouched() {
        mInteractor.steerBackward();
    }

    @Override
    public void onBackwardButtonReleased() {
        mInteractor.stop();
    }

    @Override
    public void onRightButtonTouched() {
        mInteractor.steerRight();
    }

    @Override
    public void onRightButtonReleased() {
        mInteractor.center();
    }

    @Override
    public void onLeftButtonTouched() {
        mInteractor.steerLeft();
    }
    @Override
    public void onLeftButtonReleased() {
        mInteractor.center();
    }
}
