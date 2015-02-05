package ph.edu.msuiit.rccarclient.discovery;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.models.Device;

public class DiscoveryPresenterImpl implements DiscoveryPresenter{
    private DiscoveryView mView;

    public DiscoveryPresenterImpl(DiscoveryView view){
        mView = view;
    }

    public void onRefreshClicked(){
        mView.hideError();
        mView.refreshDeviceList();
    }

    @Override
    public void discoveryStarted() {
        mView.showProgress();
    }

    @Override
    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort) {
        Device device = new Device(serverName, serverAddress);
        mView.addDevice(device);
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

    }

    @Override
    public void onEnd(){

    }

    @Override
    public void onClickWifiSettings() {
        mView.showWifiSettings();
    }

}
