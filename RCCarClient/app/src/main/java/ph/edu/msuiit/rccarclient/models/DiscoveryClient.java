package ph.edu.msuiit.rccarclient.models;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

import ph.edu.msuiit.rccarclient.common.RCCommand;
import ph.edu.msuiit.rccarclient.common.RCResponse;

public class DiscoveryClient{
    private static final String TAG = "DiscoveryClient";
    public static final int DEFAULT_TIMEOUT = 10000;
    public static final int DEFAULT_PORT = 19876;

    private OnServerFoundListener mListener;
    private InetAddress broadcastAddress;
    private int timeout;
    private int discoveryServerPort;
    private int discoveryClientPort;

    public DiscoveryClient(InetAddress broadcastAddress){
        this.broadcastAddress = broadcastAddress;
        this.timeout = DEFAULT_TIMEOUT;
        this.discoveryClientPort = DEFAULT_PORT;
        this.discoveryServerPort = DEFAULT_PORT;
    }

    public void setOnServerFoundListener(OnServerFoundListener onServerFoundListener) {
        this.mListener = onServerFoundListener;
    }

    /**
     * Starts discovery of servers connected in the same wifi network
     *
     * @throws IOException
     */
    public void startDiscovery() throws IOException{
        DatagramSocket socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        socket.setSoTimeout(timeout);
        socket.bind(new InetSocketAddress(discoveryClientPort));
        int i = 0;
        do{
            sendDiscoveryRequest(socket);
        } while (!listenForResponses(socket) && i++ < 3);
        socket.close();
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @param socket
     *          socket on which the announcement request was sent
     * @throws IOException
     */
    public boolean listenForResponses(DatagramSocket socket) throws IOException {
        boolean found = false;
        byte[] buf = new byte[1024];

        try {
            while (!Thread.interrupted()) {
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                final String s = new String(packet.getData(), 0, packet.getLength());

                try {
                    RCResponse response = RCResponse.newInstanceFromJson(s);
                    String command = response.getCommand();
                    if( command != null && command.equalsIgnoreCase("discover")) {
                        String deviceName = (String) response.getData("device_name");
                        if(deviceName != null) {
                            found = true;
                            mListener.onServerFound(deviceName, packet.getAddress(), packet.getPort());
                        }
                    }

                } catch(JsonSyntaxException e){
                    Log.e(TAG, e.getMessage());
                }

                Log.d(TAG, "Received response " + s);
                Log.d(TAG, "Source: " + packet.getAddress()+':'+packet.getPort());
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
        return found;
    }

    /**
     * Send a broadcast UDP packet containing a request for RC Car Server to
     * announce itself.
     *
     * @throws IOException
     */
    public void sendDiscoveryRequest(DatagramSocket socket) throws IOException {
        RCCommand discoveryCommand = new RCCommand("discover");
        String data = discoveryCommand.getJson();

        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
                broadcastAddress, discoveryServerPort);
        socket.send(packet);
    }

    public void setDiscoveryClientPort(int discoveryPort) {
        this.discoveryClientPort = discoveryPort;
    }

    public void setDiscoveryServerPort(int discoveryPort) {
        this.discoveryServerPort = discoveryPort;
    }


    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getTimeout() {
        return timeout;
    }

    public interface OnServerFoundListener {
        public void onServerFound(String serverName, InetAddress serverAddress, int serverPort);
    }
}