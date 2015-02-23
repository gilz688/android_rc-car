package ph.edu.msuiit.rccarclient.tcp.proto;

public interface ControlsInteractor {
    public void steerLeft();
    public void steerRight();
    public void steerForward();
    public void steerBackward();

    /*
     *  angle (-45 to 45)
     */
    public void steer(int angle);

    /*
     *  value (0 to 255)
     */
    public void move(boolean forward, int value);
}
