package ph.edu.msuiit.rccarclient.discovery;

import android.app.Activity;
import android.content.Intent;
import android.provider.Settings;

import ph.edu.msuiit.rccarclient.discovery.proto.DiscoveryInteractor;
import ph.edu.msuiit.rccarclient.models.Device;
import ph.edu.msuiit.rccarclient.tcp.TCPActivity;

public class DiscoveryInteractorImpl implements DiscoveryInteractor {
    private Activity activity;
    public DiscoveryInteractorImpl(Activity activity){
        this.activity = activity;
    }

    @Override
    public void startDiscovery(){
        Intent intent = new Intent(activity, DiscoveryService.class);
        intent.setAction(DiscoveryService.INTENT_START_DISCOVERY);
        activity.startService(intent);
    }

    @Override
    public void connectToServer(Device device){
        Intent intent = new Intent(activity, TCPActivity.class);
        intent.putExtra("device", new ParcelableDevice(device));
        activity.startActivity(intent);

        //Bundle bundle = activity.getIntent().getExtras();
        //bundle.putParcelable("device", new ParcelableDevice(device));
        //intent.putExtras(bundle);
    }

    @Override
    public void openWifiSettings() {
        activity.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
    }
}
