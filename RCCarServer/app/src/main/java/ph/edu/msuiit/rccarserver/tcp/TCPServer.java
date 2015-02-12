package ph.edu.msuiit.rccarserver.tcp;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/*
 *  Handles requests from a TCP Client
 */
public class TCPServer extends Thread {
    public static final int DEFAULT_PORT = 19877;
    private static final String TAG = "TCPServer";
    private static final int MAX_CLIENTS = 1;
    private ServerSocket serverSocket;
    private TCPServerListener mListener;
    private ThreadPoolExecutor executor;
    private int serverPort;
    private volatile boolean isRunning = false;
    private TCPServerTask serverTask;

    public TCPServer() {
        serverPort = DEFAULT_PORT;
    }

    public TCPServer(int port) {
        serverPort = port;
    }

    public synchronized void startServer(){
        Log.d(TAG, "TCPServer started");
        isRunning = true;
        executor = new ThreadPoolExecutor(MAX_CLIENTS, MAX_CLIENTS,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());;
        start();
    }

    private void openServerSocket(){
        try {
            serverSocket = new ServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 19877", e);
        }
    }

    @Override
    public void run(){
        openServerSocket();

        while (isRunning) {
            // wait for client
            Socket connectionSocket = null;
            try {
                connectionSocket = serverSocket.accept();

                // On connection establishment start,
                // add server task to executor.
                if(executor.getActiveCount() < MAX_CLIENTS) {
                    serverTask = new TCPServerTask(connectionSocket);
                    serverTask.setTCPServerListener(mListener);
                    executor.execute(serverTask);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean isStopped() {
        return this.isRunning;
    }

    public synchronized void stopServer(){
        isRunning = false;
        executor.shutdown();
        try {
            executor.shutdownNow();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setTCPServerListener(TCPServerListener listener){
        mListener = listener;
    }

    public interface TCPServerListener{
        public void onDataReceive(String line);
    }
}