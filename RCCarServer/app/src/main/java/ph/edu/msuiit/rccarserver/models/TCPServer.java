package ph.edu.msuiit.rccarserver.models;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
 *  Handles requests from a TCP Client
 */
public class TCPServer extends Thread {
    public static final int DEFAULT_PORT = 19876;
    private static final String TAG = "TCPServer";
    private ServerSocket serverSocket;
    private int serverPort;

    public TCPServer() {
        serverPort = DEFAULT_PORT;
    }

    public TCPServer(int port) {
        serverPort = port;
    }

    public void startServer(){
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!Thread.interrupted()) {
            // wait for client
            Socket connectionSocket = null;
            try {
                connectionSocket = serverSocket.accept();

                // On connection establishment start,
                // TCPServerTask will be started in the same thread
                // since only one client should be connected.
                new TCPServerTask(connectionSocket).run();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopServer(){
        interrupt();
    }
}
