package ph.edu.msuiit.rccarserver.background;

import android.util.Log;

public class TCPDataReceiver implements TCPServer.TCPServerListener{
    private static final String TAG = "TCPDataReceiver";

    @Override
    public void onDataReceive(String line){
        Log.d(TAG,"received: " + line);
    }
}
