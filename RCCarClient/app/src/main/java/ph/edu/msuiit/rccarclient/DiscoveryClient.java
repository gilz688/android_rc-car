package ph.edu.msuiit.rccarclient;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class DiscoveryClient extends Thread {
    private static final String TAG = "Discovery";
    public static final String ACTION_DISCOVERY_SERVER_FOUND = "discovery_server_found";
    public static final String ACTION_DISCOVERY_CLIENT_STARTED = "discovery_client_started";
    public static final String ACTION_DISCOVERY_CLIENT_ENDED = "discovery_client_ended";
    public static final String ACTION_DISCOVERY_CLIENT_ERROR = "discovery_client_error";

    private static final int TIMEOUT_MS = 4000;
    private static final int DISCOVERY_PORT = 19876;
    private WifiManager mWifiManager;
    private Context mContext;

    DiscoveryClient(Context context, WifiManager manager) {
        mWifiManager = manager;
        mContext = context;
    }

    public void run() {
        try {
            sendMessage(ACTION_DISCOVERY_CLIENT_STARTED);

            DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT);
            socket.setBroadcast(true);
            socket.setSoTimeout(TIMEOUT_MS);

            sendDiscoveryRequest(socket);
            listenForResponses(socket);
            sendMessage(ACTION_DISCOVERY_CLIENT_ENDED);
        } catch (IOException e) {
            sendMessage(ACTION_DISCOVERY_CLIENT_ERROR);
            Log.e(TAG, "Could not send discovery request", e);
        }
    }

    /**
     * Send a broadcast UDP packet containing a request for RC Car Server to
     * announce itself.
     *
     * @throws IOException
     */
    private void sendDiscoveryRequest(DatagramSocket socket) throws IOException {
        String data = "discover";

        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
                getBroadcastAddress(), DISCOVERY_PORT);
        socket.send(packet);
    }

    /**
     * Calculate the broadcast IP we need to send the packet along. If we send it
     * to 255.255.255.255, it never gets sent. I guess this has something to do
     * with the mobile network not wanting to do broadcast.
     */
    private InetAddress getBroadcastAddress() throws IOException {
        DhcpInfo dhcp = mWifiManager.getDhcpInfo();
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

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @param socket
     *          socket on which the announcement request was sent
     * @throws IOException
     */
    private void listenForResponses(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[1024];
        try {
            long t1 = System.currentTimeMillis();
            while (System.currentTimeMillis() - t1 <= TIMEOUT_MS && !Thread.interrupted()) {
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                final String s = new String(packet.getData(), 0, packet.getLength());
                if(!s.equals("discover"))
                    sendServerFoundMessage(s, packet.getPort(), packet.getAddress());
                Log.d(TAG, "Received response " + s);
                Log.d(TAG, "Source: " + packet.getAddress()+':'+packet.getPort());
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }

    private void sendServerFoundMessage(String serverName, int serverPort, InetAddress serverAddress) {
        Intent intent = new Intent(ACTION_DISCOVERY_SERVER_FOUND);
        intent.putExtra("serverName", serverName);
        intent.putExtra("serverPort", serverPort);
        intent.putExtra("serverAddress", serverAddress.getAddress());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }

    private void sendMessage(String action) {
        Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
    }
}