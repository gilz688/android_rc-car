package com.github.gilz688.rccarclient.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.databinding.ActivityMainBinding;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.model.ParcelableDevice;
import com.github.gilz688.rccarclient.model.RCResponse;
import com.github.gilz688.rccarclient.tcp.TCPBroadcastReceiver;
import com.github.gilz688.rccarclient.tcp.TCPService;
import com.github.gilz688.rccarclient.ui.dialogs.theme.ThemeDialog;
import com.github.gilz688.rccarclient.ui.fragment.main.MainFragment;
import com.github.gilz688.rccarclient.util.Event;

public class MainActivity extends AppCompatActivity implements TCPBroadcastReceiver.EventListener {

    private SharedViewModel sharedViewModel;
    private ActivityMainBinding binding;

    private boolean mBound;
    private TCPService mBoundService;
    private TCPBroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set App Theme
        setAppTheme();

        // Setup View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // Inject SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        // Observe when device is selected
        sharedViewModel.getDeviceData().observe(this, this::handleDeviceData);
        // Observe when angle is changed
        sharedViewModel.getAngleData().observe(this, this::steer);
        // Observe when speed is changed
        sharedViewModel.getSpeedData().observe(this, this::move);
        // Observe when horn is changed
        sharedViewModel.getHornData().observe(this, this::handleHorn);

        if (savedInstanceState == null) {
            // Display the controls
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
    }

    private void setAppTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(ThemeDialog.PREF_NAME, MODE_PRIVATE);
        switch (sharedPreferences.getInt(ThemeDialog.PREF_KEY_APP_THEME, ThemeDialog.DEFAULT_APP_THEME)) {
            case 0:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case 1:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

    private void handleHorn(Event<Boolean> event) {
        Boolean enableHorn = event.getContentIfNotHandledOrReturnNull();
        if (enableHorn == null) {
            // do nothing
        } else if (enableHorn) {
            sendHornCommand();
        } else {
            sendStopHornCommand();
        }
    }

    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mBoundService = ((TCPService.TCPServiceBinder) service).getService();
            TCPService.TCPServiceBinder binder = (TCPService.TCPServiceBinder) service;
            mBoundService = binder.getService();
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mBoundService = null;
            mBound = false;
        }
    };

    public void startTCPConnection(Device device) {
        Intent intent = new Intent(this, TCPService.class);
        intent.putExtra("device", new ParcelableDevice(device));
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    public void stopTCPConnection() {
        if(mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    public void move(int speed) {
        if(mBound) {
            mBoundService.sendCommand(TCPService.ACTION_MOVE, speed);
        }
    }

    public void steer(int angle) {
        if(mBound) {
            mBoundService.sendCommand(TCPService.ACTION_STEER, angle);
        }
    }

    public void sendHornCommand() {
        if(mBound) {
            mBoundService.sendCommand(TCPService.ACTION_HORN, "start");
        }
    }

    public void sendStopHornCommand() {
        if(mBound) {
            mBoundService.sendCommand(TCPService.ACTION_HORN, "stop");
        }
    }

    public void handleDeviceData(Device device) {
        if (device == null) {
            stopTCPConnection();
        } else {
            startTCPConnection(device);
        }
    }

    @Override
    public void onTCPConnected() {
        showToast("Connected to RC Car.");
        sharedViewModel.getConnectedData().postValue(true);
    }

    @Override
    public void onTCPConnectionFailed() {
        showToast("Unable to connect to RC Car!");
        stopTCPConnection();
    }

    @Override
    public void onTCPDisconnected() {
        showToast("RC Car has been disconnected.");
        sharedViewModel.getConnectedData().postValue(false);
        sharedViewModel.getDeviceData().postValue(null);
        stopTCPConnection();
    }

    @Override
    public void onTCPResponseReceived(RCResponse response) {
        Log.d("MainActivity", "response received: " + response.getJson());
    }

    private void showToast(String error) {
        Toast toast = Toast.makeText(getApplicationContext(),
                error, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }

    @Override
    public void onResume() {
        Log.d("MainActivity", "onResume");
        mReceiver = new TCPBroadcastReceiver(this, this);
        mReceiver.register();
        super.onResume();
    }

    @Override
    public void onPause() {
        mReceiver.register();
        super.onPause();
    }
}
