package com.github.gilz688.rccarclient.tcp;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.models.ParcelableDevice;

public class TCPActivity extends AppCompatActivity {
    private static final String TAG = "TCPActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcp);
        Log.d(TAG, "TCPActivity Started.");

        ParcelableDevice device = getIntent().getParcelableExtra("device");
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, ControlsFragment.newInstance(device))
                .commit();
    }


}
