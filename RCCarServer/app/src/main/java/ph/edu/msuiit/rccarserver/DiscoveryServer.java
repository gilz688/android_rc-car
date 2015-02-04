package ph.edu.msuiit.rccarserver;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.io.IOException;

import android.util.Log;
import android.os.Build;

public class DiscoveryServer extends Thread {

    private static final String DEVICE_NAME = Build.MODEL;
    private static final String TAG = "Discovery Server";
    private static final String DISCOVERY_MESSAGE = "discover";
    private static final int DISCOVERY_PORT = 19876;

    DatagramSocket socket;

    public DiscoveryServer (String threadName) {
        this.setName(threadName);
    }

    @Override
    public void run() {
        try {

            socket = new DatagramSocket(DISCOVERY_PORT);
            listenForRequests(DISCOVERY_MESSAGE);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }



    /**
     * Receives a broadcast UDP packet from RC Car Client for discovery. Then sends
     * the device name as a response if the expected message is received.
     *
     * @param expectedMessage
     *              an expected message should be receive from client.
     * @throws IOException
     */
    private void listenForRequests(String expectedMessage) throws IOException {
        byte[] data = new byte[1024];

        Log.d(TAG, "Listening...");

        /**
         * Trying to receive packet
         */
        while(!this.isInterrupted()) {
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            socket.receive(receivePacket);
            String dataStr = new String(receivePacket.getData(), 0, receivePacket.getLength());

            Log.d(TAG, "Data received: " +dataStr);
            Log.d(TAG, "Packet received from : "  +receivePacket.getAddress()+ ':' +receivePacket.getPort());

            /**
             * Checks if the packet data contains the expected message
             */
            if (dataStr.equalsIgnoreCase(expectedMessage)) {

                /**
                 * Sends the device name as a response
                 */
                data = DEVICE_NAME.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort());
                socket.send(sendPacket);

                Log.v(TAG, "Packet sent to: " + sendPacket.getAddress().getHostAddress());
            }
        }
    }

    public void disconnect() {
        socket.close();
        this.interrupt();
    }
}
