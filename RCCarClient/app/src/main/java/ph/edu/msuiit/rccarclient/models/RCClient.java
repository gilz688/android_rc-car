package ph.edu.msuiit.rccarclient.models;

public interface RCClient{
    public void startClient();
    public void stopClient();
    public void sendCommand(String command, int value);
    public void sendCommand(String command);
    public boolean isRunning();
}
