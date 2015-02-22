package ph.edu.msuiit.rccarclient.discovery.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface DiscoveryView {
    /*
     *  Progress bar is shown
     */
    public void showProgress();

    /*
     *  Progress bar is hidden
     */
    public void hideProgress();

    /*
     *  New device is displayed in the list
     */
    public void addDevice(Device device);

    /*
     *  Error View is displayed
     */
    public void showError(String message);

    /*
     *  Error View is hidden
     */
    public void hideError();

    /*
     *  Device list is emptied
     */
    public void emptyDeviceList();
}
