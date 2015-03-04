package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsInteractor {

    public void establishTCPConnection(Device device);

    public void steerForward();
    public void steerBackward();
    public void stop();

    public void steerRight();
    public void steerLeft();

    /*
     *  angle (-45 to 45)
     */
    public void steer(int angle);

    /*
     *  value (0 to 255)
     */
    public void move(boolean forward, int value);
}