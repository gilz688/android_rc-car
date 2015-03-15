package ph.edu.msuiit.rccarserver.background;

import android.os.Build;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import ph.edu.msuiit.rccarserver.common.RCCommand;
import ph.edu.msuiit.rccarserver.common.RCResponse;

public class DiscoveryServer extends Thread {

    private static final String DEVICE_NAME = Build.MODEL;
    private static final String TAG = "Discovery Server";
    private static final String DISCOVERY_COMMAND = "discover";
    private static final int DISCOVERY_PORT = 19876;

    DatagramSocket socket;

    public DiscoveryServer(String threadName) {
        this.setName(threadName);
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket(DISCOVERY_PORT);
            listenForRequests(DISCOVERY_COMMAND);

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Receives a broadcast UDP packet from RC Car Client for discovery. Then sends
     * the device name as a response if the expected message is received.
     *
     * @param expectedCommand the expected command should be received from client.
     * @throws IOException
     */
    private void listenForRequests(String expectedCommand) throws IOException {
        byte[] data = new byte[1024];

        Log.d(TAG, "Listening...");

        /**
         * Trying to receive packet
         */
        while (!this.isInterrupted()) {
            DatagramPacket receivePacket = new DatagramPacket(data, data.length);
            socket.receive(receivePacket);
            String dataStr = new String(receivePacket.getData(), 0, receivePacket.getLength());

            Log.d(TAG, "Data received: " + dataStr);
            Log.d(TAG, "Packet received from : " + receivePacket.getAddress() + ':' + receivePacket.getPort());

            try {
                RCCommand command = RCCommand.newInstanceFromJson(dataStr.trim());
                if (command != null) {
                    String cmd = command.getCommand();

                    /**
                     * Checks if the RCCommand data contains the expected message
                     */
                    if (cmd.equalsIgnoreCase(expectedCommand)) {
                        RCResponse response = new RCResponse(cmd);
                        /**
                         * Sends the device name in the response
                         */
                        response.putData("device_name", DEVICE_NAME);
                        data = response.getJson().getBytes();
                        DatagramPacket sendPacket = new DatagramPacket(data, data.length, receivePacket.getAddress(), receivePacket.getPort());
                        socket.send(sendPacket);

                        Log.d(TAG, "Response: " + response.getJson());
                        Log.d(TAG, "Packet sent to: " + sendPacket.getAddress().getHostAddress());
                    }
                }
            } catch (JsonSyntaxException e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public void disconnect() {
        socket.close();
        this.interrupt();
    }
}
