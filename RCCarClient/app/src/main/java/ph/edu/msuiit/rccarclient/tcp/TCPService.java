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

    public static final String MSG_STEER_FORWARD = "FORWARD";
    public static final String MSG_STOP_STEER_FORWARD = "STOP FORWARD";
    public static final String MSG_STEER_BACKWARD = "BACKWARD";
    public static final String MSG_STOP_STEER_BACKWARD = "STOP BACKWARD";
    public static final String MSG_STEER_RIGHT = "RIGHT";
    public static final String MSG_STOP_STEER_RIGHT = "STOP RIGHT";
    public static final String MSG_STEER_LEFT = "LEFT";
    public static final String MSG_STOP_STEER_LEFT = "STOP LEFT";

    public class TCPServiceBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Device device = (Device) intent.getExtras().getParcelable("device");
        startTCPClient(device.getIpAddress(), TCPClient.DEFAULT_PORT);
        Log.d(TAG, "Service Bind");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if((mClient != null) && (!mClient.isStopped())) {
            mClient.stopClient();
        }
        Log.d(TAG,"TCPService has been stopped.");
    }

    public void startTCPClient(InetAddress serverAddress, int port) {
        mClient = new TCPClient(serverAddress, port);
        mClient.start();
    }

    public void sendCommand(String command) {
        mClient.sendCommand(command);
    }
}