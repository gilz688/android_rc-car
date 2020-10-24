package com.github.gilz688.rccarclient.model;

import android.util.Log;

import com.github.gilz688.rccarclient.util.Event;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class TCPClient extends Thread implements RCClient {
    private static final String TAG = "TCPClient";

    private ExecutorService executor;
    private volatile boolean isRunning = false;

    private Socket connectionSocket;
    private final InetAddress serverAddress;
    private final int serverPort;
    private EventListener mListener;

    public TCPClient(InetAddress address, int port) {
        serverAddress = address;
        serverPort = port;
    }

    public synchronized void startClient() {
        isRunning = true;
        executor = Executors.newSingleThreadExecutor();
        start();
        Log.d(TAG, "TCPClient started.");
    }

    public void setEventListener(EventListener listener) {
        mListener = listener;
    }

    @Override
    public void run() {
        try {
            connectToDevice();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    public void connectToDevice() throws IOException {
        connectionSocket = null;
        try {
            connectionSocket = new Socket(serverAddress, serverPort);
            Log.d(TAG, "Connected to Server. IP: " + connectionSocket.getInetAddress() + ": " + connectionSocket.getPort());
            if(mListener != null) {
                mListener.onConnected();
            }
        } catch (SocketException e) {
            Log.e(TAG, e.getMessage());
            if(mListener != null) {
                mListener.onConnectionFailed();
            }
        }
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public synchronized void stopClient() {
        isRunning = false;
        executor.shutdownNow();
        try {
            if (connectionSocket != null)
                connectionSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            this.interrupt();
        }
    }

    public void sendCommand(String command, int value) {
        CommandSender cs = new CommandSender(command, value);
        Future<?> future = executor.submit(cs);
        try {
            if (future.get() == null) {
                Log.d(TAG, "Runnable is terminated after execution");
            } else {
                Log.d(TAG, "Runnable is still running after execution");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendCommand(String command) {
        CommandSender cs = new CommandSender(command);
        Future<?> future = executor.submit(cs);
        try {
            if (future.get() == null) {
                Log.d(TAG, "Runnable is terminated after execution");
            } else {
                Log.d(TAG, "Runnable is still running after execution");
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    class CommandSender implements Runnable {
        private final String command;
        private int value;
        private final boolean hasParameter;

        CommandSender(String command, int value) {
            this.command = command;
            this.value = value;
            this.hasParameter = true;
        }

        public CommandSender(String command) {
            this.command = command;
            this.hasParameter = false;
        }

        @Override
        public void run() {
            OutputStream os;
            PrintStream ps;
            try {
                RCCommand rcCommand = new RCCommand(command);
                if (hasParameter)
                    rcCommand.putData("param", value);
                os = connectionSocket.getOutputStream();
                ps = new PrintStream(os);
                ps.println(rcCommand.getJson());
                Log.d(TAG, "Command sent: " + command);
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public interface EventListener {
        void onConnected();
        void onDisconnected();
        void onConnectionFailed();
        void onMessageReceived(String message);
    }
}
