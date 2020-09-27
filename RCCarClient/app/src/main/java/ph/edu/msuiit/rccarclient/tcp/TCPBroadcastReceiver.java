package ph.edu.msuiit.rccarclient.tcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ph.edu.msuiit.rccarclient.common.RCResponse;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsPresenter;

public class TCPBroadcastReceiver extends BroadcastReceiver {
    private ControlsPresenter mPresenter;

    public TCPBroadcastReceiver(ControlsPresenter presenter){
        mPresenter = presenter;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action) {
            case TCPService.ACTION_TCP_CONNECTED:
                mPresenter.tcpConnected();
                break;
            case TCPService.ACTION_TCP_DISCONNECTED:
                mPresenter.tcpDisconnected();
                break;
            case TCPService.ACTION_TCP_RESPONSE:
                String json = intent.getStringExtra("response");
                RCResponse response = RCResponse.newInstanceFromJson(json);
                mPresenter.tcpResponseReceived(response);
                break;
            default:
        }
        Log.d("receiver", "Got message: " + intent.getAction());
    }

}
