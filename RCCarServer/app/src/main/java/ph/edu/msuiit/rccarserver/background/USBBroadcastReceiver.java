package ph.edu.msuiit.rccarserver.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;

public class USBBroadcastReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        Intent serviceIntent = new Intent(context.getApplicationContext(), RCCarService.class);
        switch(action){
            case UsbManager.ACTION_USB_DEVICE_ATTACHED:
                context.startService(serviceIntent);
                break;
            case UsbManager.ACTION_USB_DEVICE_DETACHED:
                context.stopService(serviceIntent);
                break;
            default:
        }
    }
}