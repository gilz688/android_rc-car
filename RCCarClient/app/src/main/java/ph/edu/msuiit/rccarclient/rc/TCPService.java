package ph.edu.msuiit.rccarclient.rc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.models.TCPClient;

public class TCPService extends Service {
    private static final String TAG = "TCPClient";
    public static final String ACTION_TCP_CONNECTED = "ACTION_TCP_CONNECTED";
    public static final String ACTION_TCP_DISCONNECTED = "ACTION_TCP_DISCONNECTED";
    public static final String ACTION_TCP_ERROR = "ACTION_TCP_ERROR";
    public static final String ACTION_TCP_DATA_RECEIVED = "ACTION_TCP_DATA_RECEIVED";

    private int mServerPort;
    private InetAddress mServerAddress;
    private TCPClient client;

    public TCPService(InetAddress serverAddress, int serverPort){
        mServerAddress = serverAddress;
        mServerPort = serverPort;
        }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
