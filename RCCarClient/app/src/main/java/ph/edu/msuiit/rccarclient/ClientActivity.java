package ph.edu.msuiit.rccarclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

import ph.edu.msuiit.rccarclient.discovery.DiscoveryFragment;
import ph.edu.msuiit.rccarclient.discovery.DiscoveryView;
import ph.edu.msuiit.rccarclient.utils.KitKatTweaks;


public class ClientActivity extends ActionBarActivity implements DiscoveryView.OnStatusUpdateListener {

    private static final String TAG = "ClientActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_client);
        KitKatTweaks.enableStatusBarTint(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DiscoveryFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStatusUpdate(STATUS status) {
        Log.d(TAG, status.toString());
        switch (status){
            case BUSY:
                setSupportProgressBarIndeterminateVisibility(true);
                break;
            case READY:
            case ERROR:
                setSupportProgressBarIndeterminateVisibility(false);
                break;
        }
    }
}
