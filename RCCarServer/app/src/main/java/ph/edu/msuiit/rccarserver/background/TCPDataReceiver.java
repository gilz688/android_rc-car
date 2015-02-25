package ph.edu.msuiit.rccarserver.background;

import android.util.Log;

import java.io.IOException;

import ph.edu.msuiit.rccarserver.commands.RCCar;

public class TCPDataReceiver implements TCPServer.TCPServerListener{
    private static final String TAG = "TCPDataReceiver";
    private RCCar mCar;

    public TCPDataReceiver(RCCar car){
        mCar = car;
    }

    @Override
    public void onDataReceive(String line){
        Log.d(TAG,"received: " + line);
        try {
            mCar.send(line);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
