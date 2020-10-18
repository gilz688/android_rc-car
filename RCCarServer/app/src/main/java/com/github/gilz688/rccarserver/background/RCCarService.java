package com.github.gilz688.rccarserver.background;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import com.github.gilz688.rccarserver.R;
import com.github.gilz688.rccarserver.ServerActivity;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class RCCarService extends Service {
    private static final String TAG = "RCCarService";
    public static final String ACTION_USB_PERMISSION = "com.github.gilz688.rccarserver.USB_PERMISSION";
    private final IBinder mBinder = new TCPServiceBinder();
    private TCPServer mServer;
    public static final int NOTIFICATION_ID = 19876;
    private RCCar car;
    public static final String CHANNEL_ID = "RC_CAR_CHANNEL";

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"TCPService onCreate()");

        startForeground();

        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }

        // Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // add UsbManager.requestPermission(driver.getDevice(), ..) handling here
            return;
        }

        try {
            UsbSerialPort serialPort = driver.getPorts().get(0); // Most devices have just one port (port 0)

            car = new RCCar(serialPort, connection);
            car.connect();

            DiscoveryServer dThread;
            dThread = new DiscoveryServer("DiscoveryServer");
            dThread.start();

            TCPServer.TCPServerListener mListener = new TCPDataReceiver(this, car);
            mServer = new TCPServer();
            mServer.setTCPServerListener(mListener);
            mServer.startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String name = getString(R.string.channel_name);
        String descriptionText = getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(RCCarService.CHANNEL_ID, name, importance);
        channel.setDescription(descriptionText);

        // Register the channel with the system
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void startForeground(){
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Notification notification = notificationBuilder
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("RC Car Server")
                .setContentText("Listening for incoming commands...")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        startForeground(NOTIFICATION_ID, notification);
    }

    public void hideNotification(){
        NotificationManager nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.cancel(NOTIFICATION_ID);
    }

    public void stopDiscoveryServer(){
        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[0]);

        for (Thread thread : threadArray) {
            if (thread.getName().equalsIgnoreCase("DiscoveryServer")) {
                DiscoveryServer dThread = (DiscoveryServer) thread;
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