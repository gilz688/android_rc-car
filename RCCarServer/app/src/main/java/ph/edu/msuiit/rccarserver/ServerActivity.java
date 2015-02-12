package ph.edu.msuiit.rccarserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;

import ph.edu.msuiit.rccarserver.discovery.DiscoveryServer;
import ph.edu.msuiit.rccarserver.tcp.TCPService;
import ph.edu.msuiit.rccarserver.utils.KitKatTweaks;

public class ServerActivity extends ActionBarActivity {
    private boolean mBound;
    private TCPService mService;
    public static final String TAG = "ServerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        KitKatTweaks.enableStatusBarTint(this);
        try {
            displayConnectionDetails();
        } catch (UnknownHostException e) {
            Log.e("Unknown Host", e.getMessage());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    public void startListening(View view) throws UnknownHostException {
        DiscoveryServer dThread;
        dThread = new DiscoveryServer("DiscoveryServer");
        dThread.start();

        // Start and Bind to TCPServer
        Intent intent = new Intent(this, TCPService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_start_listening);
        displayConnectionDetails();
    }

    public void cancelListening(View view) throws UnknownHostException {
        setContentView(R.layout.activity_server);
        displayConnectionDetails();

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        for (int i = 0; i < threadArray.length; i++) {
            if (threadArray[i].getName().equalsIgnoreCase("DiscoveryServer")) {
                DiscoveryServer dThread =  (DiscoveryServer) threadArray[i];
                dThread.disconnect();
            }
        }

        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }


    /**
     * Displays the SSID of the current Wi-Fi and the IP Address
     *
     * @throws UnknownHostException
     */
    private void displayConnectionDetails() throws UnknownHostException {

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        // SSID of the current Wi-Fi
        String SSID = wifiInfo.getSSID();

        // IP Address in integer format
        int ipInt = wifiInfo.getIpAddress();

        // Converts the IP Address to bytes
        byte[] ipBytes = new byte[] {
                (byte)((ipInt       ) & 0xff),
                (byte)((ipInt >>>  8) & 0xff),
                (byte)((ipInt >>> 16) & 0xff),
                (byte)((ipInt >>> 24) & 0xff)
        };

        // Converts the IP Address (from bytes) to string
        String IP = InetAddress.getByAddress(ipBytes).getHostAddress();

        TextView SSIDTextView = (TextView)findViewById(R.id.SSIDTextView);
        TextView ipTextView = (TextView)findViewById(R.id.ipTextView);

        SSIDTextView.setText("Wi-Fi Connection: " +SSID);
        ipTextView.setText("IP Address: " +IP);
    }

    @Override
    protected void onStop() {
        Log.d(TAG,"onStop()");
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TCPService.TCPServiceBinder binder = (TCPService.TCPServiceBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mService = null;
            mBound = false;
        }
    };
}
