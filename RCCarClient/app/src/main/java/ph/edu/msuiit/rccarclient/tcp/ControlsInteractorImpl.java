package ph.edu.msuiit.rccarclient.tcp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.models.ParcelableDevice;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsInteractor;

public class ControlsInteractorImpl implements ControlsInteractor {
    private Activity mActivity;
    private TCPService mBoundService;


    public ControlsInteractorImpl(Activity activity){
        mActivity = activity;
    }

    @Override
    public void startTCPConnection(Device device) {
        Intent intent = new Intent(mActivity, TCPService.class);
        intent.putExtra("device", new ParcelableDevice(device));
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopTCPConnection() {
        mActivity.unbindService(mConnection);
    }


    @Override
    public void move(int speed) {
        mBoundService.sendCommand(TCPService.ACTION_MOVE, speed);
    }

    @Override
    public void steer(int angle) {
        mBoundService.sendCommand(TCPService.ACTION_STEER, angle);
    }

    @Override
    public void sendHornCommand() {
        mBoundService.sendCommand(TCPService.ACTION_HORN);
    }

    @Override
    public void sendStopHornCommand() {
        mBoundService.sendCommand(TCPService.ACTION_STOP_HORN);
    }


    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((TCPService.TCPServiceBinder)service).getService();
            TCPService.TCPServiceBinder binder = (TCPService.TCPServiceBinder) service;
            mBoundService = binder.getService();
        }
        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
        }
    };
}
