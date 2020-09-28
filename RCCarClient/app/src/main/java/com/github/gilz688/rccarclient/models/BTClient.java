package com.github.gilz688.rccarclient.models;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.gilz688.rccarclient.common.RCCommand;

public class BTClient extends Thread implements RCClient {
    private static final String TAG = "BTClient";
    private static final String BLUETOOTH_UUID = "00001101-0000-1000-8000-00805f9b34fb";
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private boolean isRunning;
    private ExecutorService executor;

    public BTClient(BluetoothDevice mDevice) {
        this.mDevice = mDevice;
        isRunning = false;
    }

    @Override
    public void startClient() {
        isRunning = true;
        executor = Executors.newSingleThreadExecutor();
        start();
        Log.d(TAG, "TCPClient started.");
    }

    @Override
    public void run() {
        try {
            connectToDevice();
        }catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG,e.getMessage());
        }
    }

    public void connectToDevice() throws IOException {
        UUID uuid = UUID.fromString(BLUETOOTH_UUID);
        mSocket = mDevice.createRfcommSocketToServiceRecord(uuid);
        mSocket.connect();
    }

    @Override
    public void stopClient() {
        resetConnection();
    }

    @Override
    public void sendCommand(String command, int value) {
        CommandSender cs = new CommandSender(command, value);
        Future<?> future = executor.submit(cs);
        try {
            if (future.get() == null) {
                Log.d(TAG, "Runnable is terminated after execution");
            } else {
                Log.d(TAG, "Runnable is still running after execution");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendCommand(String command) {
        CommandSender cs = new CommandSender(command);
        Future<?> future = executor.submit(cs);
        try {
            if (future.get() == null) {
                Log.d(TAG, "Runnable is terminated after execution");
            } else {
                Log.d(TAG, "Runnable is still running after execution");
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    private void resetConnection() {
        if (mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {

            }
        }
    }

    class CommandSender implements Runnable {
        private String command;
        private int value;
        private boolean hasParameter;

        CommandSender (String command, int value) {
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
                if(hasParameter)
                    rcCommand.putData("param", value);
                os = mSocket.getOutputStream();
                ps = new PrintStream(os);
                ps.println(rcCommand.getJson());
                Log.d(TAG, "Command sent: " +command);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JsonSyntaxException e){
                e.printStackTrace();
            }
        }
    }
}
