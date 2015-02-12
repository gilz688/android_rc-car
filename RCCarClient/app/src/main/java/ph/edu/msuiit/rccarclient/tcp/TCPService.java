package ph.edu.msuiit.rccarclient.tcp;


import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import ph.edu.msuiit.rccarclient.models.TCPClient;

public class TCPService extends IntentService {

    public static final String INTENT_START_TCP_SERVICE = "INTENT_START_TCP_SERVICE";

    public static final String ACTION_TCP_CONNECTED = "ACTION_TCP_CONNECTED";
    public static final String ACTION_TCP_DISCONNECTED = "ACTION_TCP_DISCONNECTED";
    public static final String ACTION_TCP_ERROR = "ACTION_TCP_ERROR";
    public static final String ACTION_TCP_DATA_SENT = "ACTION_TCP_DATA_SENT";

    private static final String TAG = "TCPService";

    public TCPService() {
        super(TCPService.class.getName());
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "TCP Service started.");

        String action = intent.getAction();
        switch(action) {
            case INTENT_START_TCP_SERVICE:
                TCPClient mClient;
                InetAddress serverAddress = null;
                int serverPort = intent.getIntExtra("serverPort", TCPClient.DEFAULT_PORT);

                try {
                    serverAddress = InetAddress.getByAddress(intent.getByteArrayExtra("serverAddress"));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }

                Log.d(TAG, "Device Name: " +intent.getStringExtra("serverName"));
                Log.d(TAG, "Device Address: " +serverAddress);
                Log.d(TAG, "Device Port: " +serverPort);

                mClient = new TCPClient(serverAddress, TCPClient.DEFAULT_PORT);
                try {
                    mClient.startConnection();
                    sendMessage(ACTION_TCP_CONNECTED);
                    mClient.sendRequest("request");
                    sendMessage(ACTION_TCP_DATA_SENT);
                } catch (IOException e) {
                    sendMessage(ACTION_TCP_ERROR);
                    Log.e(TAG,e.getMessage());
                }
        }
    }

    private void sendMessage(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}
