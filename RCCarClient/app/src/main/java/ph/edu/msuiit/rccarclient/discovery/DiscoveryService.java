package ph.edu.msuiit.rccarclient.discovery;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;

import ph.edu.msuiit.rccarclient.models.DiscoveryClient;

public class DiscoveryService extends IntentService{
    public static final String PARAM_DISCOVERY_PORT = "PARAM_DISCOVERY_PORT";
    public static final String PARAM_TIMEOUT = "PARAM_TIMEOUT";

    public static final String INTENT_START_DISCOVERY = "INTENT_START_DISCOVERY";

    public static final String ACTION_DISCOVERY_SERVER_FOUND = "DISCOVERY_SERVER_FOUND";
    public static final String ACTION_DISCOVERY_CLIENT_STARTED = "DISCOVERY_CLIENT_STARTED";
    public static final String ACTION_DISCOVERY_CLIENT_ENDED = "DISCOVERY_CLIENT_ENDED";
    public static final String ACTION_DISCOVERY_CLIENT_ERROR = "DISCOVERY_CLIENT_ERROR";
    public  static final String ACTION_DISCOVERY_SERVER_NOT_FOUND = "DISCOVERY_SERVER_NOT_FOUND";
    public static final String ACTION_WIFI_OFF = "WIFI_OFF";

    private static final String TAG = "DiscoveryService";

    private volatile boolean serverFound;

    public DiscoveryService() {
        super("DiscoveryService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int discoveryPort = intent.getIntExtra(PARAM_DISCOVERY_PORT, DiscoveryClient.DEFAULT_PORT);
        int timeout = intent.getIntExtra(PARAM_TIMEOUT, DiscoveryClient.DEFAULT_TIMEOUT);
        String action = intent.getAction();
        switch(action){
            case INTENT_START_DISCOVERY:
                serverFound = false;
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                if(wifiManager.isWifiEnabled()) {
                    try {
                        sendMessage(ACTION_DISCOVERY_CLIENT_STARTED);
                        DiscoveryClient client = new DiscoveryClient(getBroadcastAddress(wifiManager));
                        client.setTimeout(timeout);
                        client.setDiscoveryPort(discoveryPort);
                        client.setOnServerFoundListener(new DiscoveryClient.OnServerFoundListener() {
                            @Override
                            public void onServerFound(String serverName, InetAddress serverAddress, int serverPort) {
                                serverFound = true;
                                sendServerFoundMessage(serverName, serverAddress, serverPort);
                            }
                        });
                        client.startDiscovery();
                        if (!serverFound)
                            sendMessage(ACTION_DISCOVERY_SERVER_NOT_FOUND);
                    } catch (IOException e) {
                        sendMessage(ACTION_DISCOVERY_CLIENT_ERROR);
                        Log.e(TAG,e.getMessage());
                    } finally {
                        sendMessage(ACTION_DISCOVERY_CLIENT_ENDED);
                    }
                }
                else{
                    sendMessage(ACTION_WIFI_OFF);
                }
                break;
            default:
        }
    }

    /**
     * Calculate the broadcast IP we need to send the packet along. If we send it
     * to 255.255.255.255, it never gets sent because some routers will block it by default.
     */
    private InetAddress getBroadcastAddress(WifiManager wifiManager) throws IOException {

        DhcpInfo dhcp = wifiManager.getDhcpInfo();
        if (dhcp == null) {
            Log.d(TAG, "Could not get dhcp info");
            return null;
        }

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    private void sendServerFoundMessage(String serverName, InetAddress serverAddress, int serverPort) {
        Intent intent = new Intent(ACTION_DISCOVERY_SERVER_FOUND);
        intent.putExtra("serverName", serverName);
        intent.putExtra("serverPort", serverPort);
        intent.putExtra("serverAddress", serverAddress.getAddress());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    private void sendMessage(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
}