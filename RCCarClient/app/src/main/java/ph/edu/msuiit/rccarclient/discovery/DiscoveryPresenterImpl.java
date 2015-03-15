package ph.edu.msuiit.rccarclient.discovery;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryInteractor;
import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryPresenter;
import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryView;
import ph.edu.msuiit.rccarclient.models.Device;

public class DiscoveryPresenterImpl implements DiscoveryPresenter {
    private DiscoveryView mView;
    private DiscoveryInteractor mInteractor;

    public DiscoveryPresenterImpl(DiscoveryView view, DiscoveryInteractor interactor){
        mView = view;
        mInteractor = interactor;
    }

    public void onRefreshClicked(){
        mView.hideError();
        mView.emptyDeviceList();
        mInteractor.startDiscovery();
    }

    @Override
    public void discoveryStarted() {
        mView.showProgress();
    }

    @Override
    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort) {
        Device device = new Device(serverName, serverAddress, serverPort);
        mView.addDevice(device);
        mView.hideProgress();
    }

    @Override
    public void discoveryError(String message) {
        mView.showError(message);
    }

    @Override
    public void discoveryEnded() {
        mView.hideProgress();
    }

    @Override
    public void onStart() {
        mInteractor.startDiscovery();
    }

    @Override
    public void onEnd(){

    }

    @Override
    public void onClickWifiSettings() {
        mInteractor.openWifiSettings();
    }

    @Override
    public void onItemClicked(Device device) {
        mInteractor.connectToServer(device);
    }
}
