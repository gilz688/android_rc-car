package ph.edu.msuiit.rccarserver.background;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.Set;

import ph.edu.msuiit.rccarserver.R;
import ph.edu.msuiit.rccarserver.ServerActivity;
import tw.com.prolific.driver.pl2303.PL2303Driver;

public class RCCarService extends Service {
    private static final String TAG = "RCCarService";
    public static final String ACTION_USB_PERMISSION = "ph.edu.msuiit.rccarserver.USB_PERMISSION";
    private final IBinder mBinder = new TCPServiceBinder();
    private TCPServer mServer;
    private TCPServer.TCPServerListener mListener;
    private static final int NOTIFICATION_ID = 19876;
    private RCCar car;

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"TCPService onCreate()");

        showNotification();

        PL2303Driver mSerial = new PL2303Driver((UsbManager) getSystemService(Context.USB_SERVICE),
                this, ACTION_USB_PERMISSION);
        car = new RCCar(mSerial);
        try {
            car.connect();

            DiscoveryServer dThread;
            dThread = new DiscoveryServer("DiscoveryServer");
            dThread.start();

            mListener = new TCPDataReceiver(this,car);
            mServer = new TCPServer();
            mServer.setTCPServerListener(mListener);
            mServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showNotification(){
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, "ph.edu.msuiit.rccarserver")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("RC Car Server")
                        .setContentText("Listening for incoming commands...")
                        .setOngoing(true);

        Intent targetIntent = new Intent(this, ServerActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(NOTIFICATION_ID, builder.build());
    }

    public void hideNotification(){
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(NOTIFICATION_ID);
    }

    public void stopDiscoveryServer(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);

        for (int i = 0; i < threadArray.length; i++) {
            if (threadArray[i].getName().equalsIgnoreCase("DiscoveryServer")) {
                DiscoveryServer dThread =  (DiscoveryServer) threadArray[i];
                dThread.disconnect();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"TCPService onDestroy()");
        hideNotification();
        stopDiscoveryServer();
        if(mServer != null)
            mServer.stopServer();
        if(car != null)
            car.disconnect();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class TCPServiceBinder extends Binder {
        public RCCarService getService() {
            return RCCarService.this;
        }
    }
}