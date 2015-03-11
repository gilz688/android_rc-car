package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsInteractor {

    public void startTCPConnection(Device device);
    public void stopTCPConnection();

    /*
     *  value (-255 to 255)
     */
    public void move(int speed);

    /*
     *  angle (-60 to 60)
     */
    public void steer(int angle);
}