package ph.edu.msuiit.rccarserver.models;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/*
 *  Handles a client connection
 */
public class TCPServerTask implements Runnable{
    private static final String TAG = "TCPServerTask";
    Socket mSocket;

    public TCPServerTask(Socket connectionSocket) {
        mSocket = connectionSocket;
    }

    @Override
    public void run(){
        Log.d(TAG, "Client with IP " + mSocket.getInetAddress() + " has connected to server.");
        BufferedReader socketInput = null;
        DataOutputStream socketOutput = null;
        try{
            socketInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
            socketOutput = new DataOutputStream(mSocket.getOutputStream());
            startListening(socketInput, socketOutput);
            mSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            assert socketInput != null;
            assert socketOutput != null;
            try {
                socketInput.close();
                socketOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void startListening(BufferedReader in, DataOutputStream out) throws IOException {
        while(mSocket.isConnected()){
            while(!in.ready()){

            }
        }
    }
}
