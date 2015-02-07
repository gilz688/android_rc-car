package ph.edu.msuiit.rccarclient.models;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;

public class DiscoveryClient{
    private static final String TAG = "DiscoveryClient";
    public static final int DEFAULT_TIMEOUT = 10000;
    public static final int DEFAULT_PORT = 19876;

    private OnServerFoundListener mListener;
    private InetAddress broadcastAddress;
    private int timeout;
    private int discoveryPort;

    public DiscoveryClient(InetAddress broadcastAddress){
        this.broadcastAddress = broadcastAddress;
        this.timeout = DEFAULT_TIMEOUT;
        this.discoveryPort = DEFAULT_PORT;
    }

    public void setOnServerFoundListener(OnServerFoundListener mListener) {
        this.mListener = mListener;
    }

    /**
     * Starts discovery of servers connected in the same wifi network
     *
     * @throws java.io.IOException
     */
    public void startDiscovery() throws IOException{
        DatagramSocket socket = new DatagramSocket(null);
        socket.setReuseAddress(true);
        socket.setBroadcast(true);
        socket.setSoTimeout(timeout);
        socket.bind(new InetSocketAddress(discoveryPort));
        sendDiscoveryRequest(socket);
        listenForResponses(socket);
        socket.close();
    }

    /**
     * Listen on socket for responses, timing out after TIMEOUT_MS
     *
     * @param socket
     *          socket on which the announcement request was sent
     * @throws java.io.IOException
     */
    public void listenForResponses(DatagramSocket socket) throws IOException {
        byte[] buf = new byte[1024];
        try {
            while (!Thread.interrupted()) {
                final DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                final String s = new String(packet.getData(), 0, packet.getLength());
                if(!s.equals("discover"))
                    mListener.onServerFound(s, packet.getAddress(), packet.getPort());

                Log.d(TAG, "Received response " + s);
                Log.d(TAG, "Source: " + packet.getAddress()+':'+packet.getPort());
            }
        } catch (SocketTimeoutException e) {
            Log.d(TAG, "Receive timed out");
        }
    }

    /**
     * Send a broadcast UDP packet containing a request for RC Car Server to
     * announce itself.
     *
     * @throws IOException
     */
    public void sendDiscoveryRequest(DatagramSocket socket) throws IOException {
        String data = "discover";
        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.length(),
                broadcastAddress, discoveryPort);
        socket.send(packet);
    }

    public void setDiscoveryPort(int discoveryPort) {
        this.discoveryPort = discoveryPort;
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