package ph.edu.msuiit.rccarserver.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import ph.edu.msuiit.rccarserver.models.TCPServer;

public class TCPService extends Service {
    private final IBinder mBinder = new TCPServiceBinder();
    private TCPServer mServer;

    @Override
    public void onCreate(){
        mServer = new TCPServer();
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
        TCPService getService() {
            return TCPService.this;
        }
    }
}
