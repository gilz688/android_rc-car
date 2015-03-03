package ph.edu.msuiit.rccarclient.tcp;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.models.ParcelableDevice;
import ph.edu.msuiit.rccarclient.utils.KitKatTweaks;

public class TCPActivity extends ActionBarActivity{
    private static final String TAG = "TCPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        KitKatTweaks.enableStatusBarTint(this);
        Log.d(TAG, "TCPActivity Started.");

        ParcelableDevice device = getIntent().getParcelableExtra("device");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ControlsFragment.newInstance(device))
                .commit();
    }
}
