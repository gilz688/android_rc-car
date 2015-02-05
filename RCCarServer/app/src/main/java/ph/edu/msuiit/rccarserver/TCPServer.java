package ph.edu.msuiit.rccarserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.io.DataInputStream;
import java.io.IOException;
import android.util.Log;

public class TCPServer extends Thread {
    private static final String TAG = "TCP Server";
    ServerSocket mServerSocket;
    Socket mSocket;
    int mPort;

    public TCPServer(int port) {
        this.mPort = port;
    }

    @Override
    public void run() {
        try {
            mServerSocket = new ServerSocket(mPort);
            try {
                mSocket = mServerSocket.accept();
                while (true) {

                }

            } catch(SocketException e) {
                Log.e(TAG, "Socket problem", e);
            }

        } catch (UnknownHostException e) {
            Log.e(TAG, "Unknown Host", e);

        } catch (IOException e) {
            Log.e(TAG, "Could not receive data", e);
        }
    }
}
