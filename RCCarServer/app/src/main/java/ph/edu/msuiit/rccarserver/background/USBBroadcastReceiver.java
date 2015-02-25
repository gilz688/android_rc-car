package ph.edu.msuiit.rccarserver.background;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.widget.Toast;

public class USBBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        Intent serviceIntent = new Intent(context.getApplicationContext(), RCCarService.class);
        switch(action){
            case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                PendingIntent permissionIntent = PendingIntent.getBroadcast(context, 0, new  Intent(RCCarService.ACTION_USB_PERMISSION), 0);
                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
                usbManager.requestPermission(device, permissionIntent);
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED:
                context.stopService(serviceIntent);
                break;
            case RCCarService.ACTION_USB_PERMISSION:
                Toast.makeText(context,"Permission Granted!",Toast.LENGTH_SHORT).show();
                context.startService(serviceIntent);
            default:
        }
    }
}