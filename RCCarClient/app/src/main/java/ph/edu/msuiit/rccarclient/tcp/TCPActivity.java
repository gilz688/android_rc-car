package ph.edu.msuiit.rccarclient.tcp;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.utils.KitKatTweaks;

public class TCPActivity extends ActionBarActivity {
    private static final String TAG = "TCPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        KitKatTweaks.enableStatusBarTint(this);
        Device device = (Device) getIntent().getExtras().getParcelable("device");
        Log.d(TAG, "TCPActivity Started.");
        registerTCPBroadcastReceiver();
        establishTCPConnection(device.getName(), device.getIpAddress(), device.getPort());
    }


    private void establishTCPConnection(String name, InetAddress serverAddress, int serverPort) {
        Intent intent = new Intent(this, TCPService.class);
        intent.setAction(TCPService.INTENT_START_TCP_SERVICE);
        intent.putExtra("serverName", name);
        intent.putExtra("serverAddress",serverAddress.getAddress());
        intent.putExtra("serverPort", serverPort);
        startService(intent);
    }

    private void registerTCPBroadcastReceiver(){
        TCPBroadcastReceiver tcpBroadcastReceiver = new TCPBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TCPService.ACTION_TCP_CONNECTED);
        filter.addAction(TCPService.ACTION_TCP_DISCONNECTED);
        filter.addAction(TCPService.ACTION_TCP_ERROR);
        filter.addAction(TCPService.ACTION_TCP_DATA_SENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(tcpBroadcastReceiver,
                filter);
    }
}
