package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsInteractor {

    public void establishTCPConnection(Device device);

    public void steerForward();
    public void stopSteerForward();

    public void steerBackward();
    public void stopSteerBackward();

    public void steerRight();
    public void stopSteerRight();

    public void steerLeft();
    public void stopSteerLeft();
    /*
     *  angle (-45 to 45)
     */
    public void steer(int angle);

    /*
     *  value (0 to 255)
     */
    public void move(boolean forward, int value);
}