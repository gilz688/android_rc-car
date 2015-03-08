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
    private ServiceConnection mConnection;

    public static final int RC_CAR_SPEED = 255;

    public ControlsInteractorImpl(Activity activity){
        mActivity = activity;

        mConnection = new ServiceConnection() {
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


    @Override
    public void establishTCPConnection(Device device) {
        Intent intent = new Intent(mActivity, TCPService.class);
        intent.putExtra("device", new ParcelableDevice(device));
        mActivity.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void stopTCPConnection() {
        mActivity.unbindService(mConnection);
    }


    @Override
    public void steerForward() {
        move(true, RC_CAR_SPEED);
    }

    @Override
    public void steerBackward() {
        move(false, RC_CAR_SPEED);
    }

    @Override
    public void stop() {
        mBoundService.sendStopCommand();
    }


    @Override
    public void steerRight(int angle) {
        mBoundService.sendCommand(TCPService.MSG_STEER_RIGHT, angle);
    }

    @Override
    public void steerLeft(int angle) {
        mBoundService.sendCommand(TCPService.MSG_STEER_RIGHT, angle);
    }

    @Override
    public void center() {
        mBoundService.sendCenterCommand();
    }

    public void move(boolean forward, int speed) {
        if(forward) {
            mBoundService.sendCommand(TCPService.MSG_STEER_FORWARD, speed);
        }
        else {
            mBoundService.sendCommand(TCPService.MSG_STEER_BACKWARD, speed);
        }
    }
}
