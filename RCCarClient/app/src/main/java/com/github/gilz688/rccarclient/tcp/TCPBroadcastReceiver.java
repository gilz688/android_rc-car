package com.github.gilz688.rccarclient.tcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.gilz688.rccarclient.model.RCResponse;

public class TCPBroadcastReceiver extends BroadcastReceiver {
    private final Context context;
    private final EventListener mListener;
    private final LocalBroadcastManager broadcastManager;

    public TCPBroadcastReceiver(Context context, EventListener listener) {
        this.context = context;
        broadcastManager = LocalBroadcastManager.getInstance(context);
        mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case TCPService.ACTION_TCP_CONNECTED:
                mListener.onTCPConnected();
                break;
            case TCPService.ACTION_TCP_CONNECTION_FAILED:
                mListener.onTCPConnectionFailed();
                break;
            case TCPService.ACTION_TCP_DISCONNECTED:
                mListener.onTCPDisconnected();
                break;
            case TCPService.ACTION_TCP_RESPONSE:
                String json = intent.getStringExtra("response");
                RCResponse response = RCResponse.newInstanceFromJson(json);
                mListener.onTCPResponseReceived(response);
                break;
            default:
        }
        Log.d("receiver", "Got message: " + intent.getAction());
    }

    public void register() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(TCPService.ACTION_TCP_CONNECTED);
        filter.addAction(TCPService.ACTION_TCP_CONNECTION_FAILED);
        filter.addAction(TCPService.ACTION_TCP_DISCONNECTED);
        filter.addAction(TCPService.ACTION_TCP_RESPONSE);
        broadcastManager.registerReceiver(this, filter);
    }

    public void unregister() {
        broadcastManager.unregisterReceiver(this);
    }

    public interface EventListener {
        void onTCPConnected();
        void onTCPConnectionFailed();
        void onTCPDisconnected();
        void onTCPResponseReceived(RCResponse response);
    }
}
