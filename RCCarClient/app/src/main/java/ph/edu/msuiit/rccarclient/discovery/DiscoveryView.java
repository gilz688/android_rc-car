package ph.edu.msuiit.rccarclient.discovery;

import ph.edu.msuiit.rccarclient.models.Device;

public interface DiscoveryView {
    public void showProgress();
    public void hideProgress();
    public void addDevice(Device device);
    public void refreshDeviceList();
    public void showError(String message);
    public void hideError();
    public void showWifiSettings();

    public interface OnStatusUpdateListener{
        public enum STATUS{ READY, BUSY, ERROR };

        public void onStatusUpdate(STATUS status);
    }
}
