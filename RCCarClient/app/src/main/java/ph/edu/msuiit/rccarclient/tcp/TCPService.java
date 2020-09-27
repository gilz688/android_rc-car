package ph.edu.msuiit.rccarclient.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.models.RCClient;
import ph.edu.msuiit.rccarclient.models.TCPClient;

public class TCPService extends Service {
    private static final String TAG = "TCPService";
    public static final String ACTION_HORN = "horn";
    public static final String ACTION_STOP_HORN = "stop horn";
    public static final String ACTION_TCP_CONNECTED = "ACTION_TCP_CONNECTED";
    public static final String ACTION_TCP_DISCONNECTED = "ACTION_TCP_DISCONNECTED";
    public static final String ACTION_TCP_RESPONSE = "ACTION_TCP_RESPONSE";
    private final IBinder mBinder = new TCPServiceBinder();
    private RCClient mClient;

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

    private void sendMessage(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}