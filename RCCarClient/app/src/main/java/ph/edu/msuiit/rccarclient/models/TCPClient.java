package ph.edu.msuiit.rccarclient.models;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class TCPClient {
    private static final String TAG = "TCPClient";
    public static final int DEFAULT_PORT = 19876;

    private Socket mSocket;
    private InetAddress remoteAddress;
    private int port;


    public TCPClient(InetAddress remoteAddress){
        this.remoteAddress = remoteAddress;
        this.port = DEFAULT_PORT;
    }

    public TCPClient(InetAddress remoteAddress, int port) {
        this.remoteAddress = remoteAddress;
        this.port = port;
    }

    public void startConnection() throws IOException {
        mSocket = null;
        try {
            mSocket= new Socket(remoteAddress, port);
            Log.d(TAG, "Connected to Server. IP: " +mSocket.getInetAddress()+ ": " +mSocket.getPort());
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendRequest(String message) throws IOException {
        byte[] buff = message.getBytes();
        DataOutputStream output = new DataOutputStream(mSocket.getOutputStream());
        output.write(buff);
        Log.d(TAG, "Message: " +message);
    }

    public boolean isSocketConnected() {
        return mSocket.isConnected();
    }

    public void disconnect() throws IOException {
        mSocket.close();
    }
}
