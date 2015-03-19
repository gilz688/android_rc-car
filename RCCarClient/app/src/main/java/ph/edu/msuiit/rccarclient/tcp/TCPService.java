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
    public static final String ACTION_HORN = "horn";
    private final IBinder mBinder = new TCPServiceBinder();
    private TCPClient mClient;

    public static final String ACTION_MOVE = "move";
    public static final String ACTION_STEER = "steer";

    public class TCPServiceBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Device device = (Device) intent.getParcelableExtra("device");
        mClient = new TCPClient(device.getIpAddress());
        mClient.startClient();
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

    public void sendCommand(String command, int value) {
        mClient.sendCommand(command, value);
    }

    public void sendCommand(String command) { mClient.sendCommand(command); }
}