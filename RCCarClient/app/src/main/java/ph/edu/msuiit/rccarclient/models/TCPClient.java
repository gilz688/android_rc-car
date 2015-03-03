package ph.edu.msuiit.rccarclient.models;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class TCPClient extends Thread {
    private static final String TAG = "TCPClient";
    public static final int DEFAULT_PORT = 19876;

    private Socket connectionSocket;
    private InetAddress serverAddress;
    private int serverPort;

    public TCPClient(InetAddress serverAddress){
        this.serverAddress = serverAddress;
        serverPort = DEFAULT_PORT;
    }

    public TCPClient(InetAddress address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    @Override
    public void run() {
        try {
            connectToServer();
        }catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    public void connectToServer() throws IOException {
        connectionSocket = null;
        try {
            connectionSocket = new Socket(serverAddress, serverPort);
            Log.d(TAG, "Connected to Server. IP: " +connectionSocket.getInetAddress()+ ": " +connectionSocket.getPort());
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void sendCommand(String command) {
        try {
            OutputStream os = connectionSocket.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.println(command);
            Log.d(TAG, "Command sent: " +command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isStopped() {
        return (!connectionSocket.isConnected() && this.isInterrupted());
    }

    public void stopClient() {
        try {
            connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();
        }
    }
}
