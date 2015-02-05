package ph.edu.msuiit.rccarclient;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ph.edu.msuiit.rccarclient.discovery.DiscoveryFragment;
import ph.edu.msuiit.rccarclient.discovery.DiscoveryView;
import ph.edu.msuiit.rccarclient.utils.KitKatTweaks;


public class ClientActivity extends ActionBarActivity implements DiscoveryView.OnStatusUpdateListener {

    private static final String TAG = "ClientActivity";
    private SmoothProgressBar spbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);
        KitKatTweaks.enableStatusBarTint(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        spbToolbar = (SmoothProgressBar) findViewById(R.id.spbToolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DiscoveryFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return super.onCreateOptionsMenu(menu);
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
                spbToolbar.setVisibility(View.VISIBLE);
                break;
            case READY:
                spbToolbar.setVisibility(View.GONE);
                break;
            case ERROR:
                spbToolbar.setVisibility(View.GONE);
                break;
        }
    }
}
