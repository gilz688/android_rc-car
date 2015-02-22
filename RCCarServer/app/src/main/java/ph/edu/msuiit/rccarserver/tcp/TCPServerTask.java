package ph.edu.msuiit.rccarserver.tcp;

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
    private Socket mSocket;
    private TCPServer.TCPServerListener mListener;

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
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try {
                if(socketInput != null)
                    socketInput.close();
                if(socketOutput != null)
                    socketOutput.close();
                if(mSocket != null)
                    mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d(TAG, "Client with IP " + mSocket.getInetAddress().getHostAddress() + " has disconnected.");
        }
    }

    private void startListening(BufferedReader in, DataOutputStream out) throws IOException {
        String line;
        while((line = in.readLine()) != null){
            mListener.onDataReceive(line);
        }
    }

    public synchronized void setTCPServerListener(TCPServer.TCPServerListener listener){
        mListener = listener;
    }

}