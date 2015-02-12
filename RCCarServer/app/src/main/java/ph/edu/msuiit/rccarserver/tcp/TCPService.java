package ph.edu.msuiit.rccarserver.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TCPService extends Service {
    private static final String TAG = "TCPService";
    private final IBinder mBinder = new TCPServiceBinder();
    private TCPServer mServer;
    private TCPServer.TCPServerListener mListener;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"TCPService onCreate()");
        mListener = new TCPDataReceiver();
        mServer = new TCPServer();
        mServer.setTCPServerListener(mListener);
        mServer.startServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"TCPService onDestroy()");
        if(mServer != null)
            mServer.stopServer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class TCPServiceBinder extends Binder {
        public TCPService getService() {
            return TCPService.this;
        }
    }
}
