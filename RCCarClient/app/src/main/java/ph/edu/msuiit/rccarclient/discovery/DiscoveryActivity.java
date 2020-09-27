package ph.edu.msuiit.rccarclient.discovery;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import ph.edu.msuiit.rccarclient.R;

public class DiscoveryActivity extends AppCompatActivity implements DiscoveryFragment.OnStatusUpdateListener {

    private static final String TAG = "DiscoveryActivity";
    private static final String STATE_IS_PROGRESS_VISIBLE = "STATE_IS_PROGRESS_VISIBLE";
    private ProgressBar spbToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        Toolbar toolbar = findViewById(R.id.toolbar);
        spbToolbar = findViewById(R.id.spbToolbar);
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
        // automatically handle clicks on the Home/Up material_button, so long
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

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        if(savedState != null) {
            boolean visible = savedState.getBoolean(STATE_IS_PROGRESS_VISIBLE, false);
            if(spbToolbar != null & visible)
                spbToolbar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_IS_PROGRESS_VISIBLE,spbToolbar.getVisibility()==View.VISIBLE);
    }
}
