package ph.edu.msuiit.rccarclient.tcp;

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
    public void onLeftButtonClick() {

    }

    @Override
    public void onRightButtonClick() {

    }

    @Override
    public void onForwardButtonClick() {

    }

    @Override
    public void onBackwardButtonClick() {

    }
}
