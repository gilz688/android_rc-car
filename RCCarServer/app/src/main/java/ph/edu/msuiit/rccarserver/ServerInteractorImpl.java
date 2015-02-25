package ph.edu.msuiit.rccarserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;

import ph.edu.msuiit.rccarserver.background.RCCarService;
import ph.edu.msuiit.rccarserver.background.USBBroadcastReceiver;

public class ServerInteractorImpl implements ph.edu.msuiit.rccarserver.proto.ServerInteractor {
    private Context mContext;
    private ServiceConnection mConnection;

    public ServerInteractorImpl(Context context, ServiceConnection connection){
        mContext = context;
        mConnection = connection;
    }

    @Override
    public void stopRCServer(){
        Context appContext = mContext.getApplicationContext();
        Intent serviceIntent = new Intent(appContext.getApplicationContext(), RCCarService.class);
        appContext.getApplicationContext().stopService(serviceIntent);
    }

    @Override
    public void enableRCService(){
        ComponentName receiver = new ComponentName(mContext, USBBroadcastReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public void disableRCService(){
        ComponentName receiver = new ComponentName(mContext, USBBroadcastReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    @Override
    public boolean isRCServiceEnabled(){
        ComponentName receiver = new ComponentName(mContext, USBBroadcastReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        boolean enabled = false;
        int enabledSetting = pm.getComponentEnabledSetting(receiver);
        switch(enabledSetting){
            case PackageManager.COMPONENT_ENABLED_STATE_ENABLED:
                enabled = true;
            default:
        }
        return enabled;
    }
}