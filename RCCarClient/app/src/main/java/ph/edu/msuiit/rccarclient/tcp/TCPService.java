package ph.edu.msuiit.rccarclient.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.models.TCPClient;

public class TCPService extends Service {
    private static final String TAG = "TCPService";
    private final IBinder mBinder = new TCPServiceBinder();
    private TCPClient mClient;

    public static final String MSG_STEER_FORWARD = "forward";
    public static final String MSG_STEER_BACKWARD = "backward";
    public static final String MSG_STOP = "stop";

    public static final String MSG_STEER_RIGHT = "right";
    public static final String MSG_STEER_LEFT = "left";
    public static final String MSG_CENTER = "center";

    public class TCPServiceBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Device device = (Device) intent.getParcelableExtra("device");
        startTCPClient(device.getIpAddress(), device.getPort());
        Log.d(TAG, "Service Bind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if((mClient != null) && (mClient.isRunning())) {
            mClient.stopClient();
            Log.d(TAG,"TCPService has been stopped.");
        }
    }

    public void startTCPClient(InetAddress serverAddress, int port) {
        mClient = new TCPClient(serverAddress, port);
        mClient.startClient();
    }

    public void sendCommand(String command, int value) {
        mClient.sendCommand(command + "[" + value + "]");
    }

    public void sendStopCommand() {
        mClient.sendCommand(MSG_STOP);
    }

    public void sendCenterCommand() {
        mClient.sendCommand(MSG_CENTER);
    }
}