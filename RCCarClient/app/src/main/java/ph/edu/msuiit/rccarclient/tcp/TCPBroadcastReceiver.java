package ph.edu.msuiit.rccarclient.tcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TCPBroadcastReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        switch (action) {
            case TCPService.ACTION_TCP_CONNECTED:
                //some actions here
                break;

            case TCPService.ACTION_TCP_DISCONNECTED:
                //some actions here
                break;

            case TCPService.ACTION_TCP_ERROR:
                //some actions here
                break;

            case TCPService.ACTION_TCP_DATA_SENT:
                //some actions here
                Log.d("TCP Broadcast Receiver", "A message has been sent");
                break;
        }

        Log.d("TCP Broadcast Receiver", "Message received: " + intent.getAction());

    }
}
