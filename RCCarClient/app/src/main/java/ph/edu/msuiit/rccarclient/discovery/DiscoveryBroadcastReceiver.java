package ph.edu.msuiit.rccarclient.discovery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DiscoveryBroadcastReceiver extends BroadcastReceiver {
    private DiscoveryPresenter mPresenter;

    public DiscoveryBroadcastReceiver(DiscoveryPresenter presenter){
        mPresenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case DiscoveryService.ACTION_DISCOVERY_SERVER_FOUND:
                // Extract data included in the Intent
                String serverName = intent.getStringExtra("serverName");
                InetAddress serverAddress = null;
                try {
                    serverAddress = InetAddress.getByAddress(intent.getByteArrayExtra("serverAddress"));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                int serverPort = intent.getIntExtra("serverPort", -1);

                mPresenter.onDeviceFound(serverName, serverAddress, serverPort);
                break;
            case DiscoveryService.ACTION_DISCOVERY_CLIENT_STARTED:
                mPresenter.discoveryStarted();
                break;
            case DiscoveryService.ACTION_DISCOVERY_CLIENT_ENDED:
                mPresenter.discoveryEnded();
                break;
            case DiscoveryService.ACTION_DISCOVERY_CLIENT_ERROR:
                mPresenter.discoveryError();
                break;
        }
        Log.d("receiver", "Got message: " + intent.getAction());
    }

}
