package ph.edu.msuiit.rccarclient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class TCPClient extends Thread{
    private static final String TAG = "TCPClient";
    public static final String ACTION_TCP_CONNECTED = "ACTION_TCP_CONNECTED";
    public static final String ACTION_TCP_DISCONNECTED = "ACTION_TCP_DISCONNECTED";
    public static final String ACTION_TCP_ERROR = "ACTION_TCP_ERROR";
    public static final String ACTION_TCP_DATA_RECEIVED = "ACTION_TCP_DATA_RECEIVED";

    private int mServerPort;
    private InetAddress mServerAddress;

    public TCPClient(InetAddress serverAddress, int serverPort){
        mServerAddress = serverAddress;
        mServerPort = serverPort;;
        }

    public void run() {
        try {
            Socket socket = new Socket(mServerAddress, mServerPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
