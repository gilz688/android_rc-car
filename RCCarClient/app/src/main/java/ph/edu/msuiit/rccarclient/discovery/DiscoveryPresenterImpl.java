package ph.edu.msuiit.rccarclient.discovery;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.models.DiscoveryClient;

public class DiscoveryPresenterImpl implements DiscoveryPresenter{
    private DiscoveryView mView;
    private DiscoveryClient mClient;

    public DiscoveryPresenterImpl(DiscoveryView view){
        mView = view;
    }

    public void onRefreshButtonClicked(){
        mView.refreshDeviceList();
    }

    @Override
    public void onDeviceFound(String serverName, InetAddress serverAddress, int serverPort) {
        Device device = new Device(serverName, serverAddress);
        mView.addDevice(device);
    }

    @Override
    public void discoveryStarted() {
        mView.showProgress();
    }

    @Override
    public void discoveryError() {
        mView.hideProgress();
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

}
