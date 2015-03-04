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



    public ControlsInteractorImpl(Activity activity, TCPService boundService){
        mActivity = activity;
        mBoundService = boundService;

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
    public void steerForward() {
        mBoundService.sendCommand(TCPService.MSG_STEER_FORWARD);
    }
    @Override
    public void stopSteerForward() {
        mBoundService.sendCommand(TCPService.MSG_STOP_STEER_FORWARD);
    }



    @Override
    public void steerBackward() {
        mBoundService.sendCommand(TCPService.MSG_STEER_BACKWARD);
    }
    @Override
    public void stopSteerBackward() {
        mBoundService.sendCommand(TCPService.MSG_STOP_STEER_BACKWARD);
    }



    @Override
    public void steerRight() {
        mBoundService.sendCommand(TCPService.MSG_STEER_RIGHT);
    }
    @Override
    public void stopSteerRight() {
        mBoundService.sendCommand(TCPService.MSG_STOP_STEER_RIGHT);
    }



    @Override
    public void steerLeft() {
        mBoundService.sendCommand(TCPService.MSG_STEER_LEFT);
    }
    @Override
    public void stopSteerLeft() {
        mBoundService.sendCommand(TCPService.MSG_STOP_STEER_LEFT);
    }



    @Override
    public void steer(int angle) {

    }

    @Override
    public void move(boolean forward, int value) {

    }
}
