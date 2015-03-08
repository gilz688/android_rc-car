package ph.edu.msuiit.rccarclient.tcp.proto;

import ph.edu.msuiit.rccarclient.models.Device;

public interface ControlsInteractor {

    public void establishTCPConnection(Device device);
    public void stopTCPConnection();

    public void steerForward();
    public void steerBackward();
    public void stop();

    public void steerRight(int angle);
    public void steerLeft(int angle);
    public void center();

    /*
     *  angle (-45 to 45)
     */
    //public void steer(int angle);

    /*
     *  value (0 to 255)
     */
    //public void move(boolean forward, int value);

}