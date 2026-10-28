package com.github.gilz688.rccarclient.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.api.RCCarService;
import com.github.gilz688.rccarclient.databinding.ActivityMainBinding;
import com.github.gilz688.rccarclient.model.RCCommand;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set App Theme
        setAppTheme();

        // Setup View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (savedInstanceState == null) {
            // Display the controls
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ControlsFragment())
                    .commit();
        }

        OkHttpClient okClient = new OkHttpClient();

        RCCarService rcCarService = new Scarlet.Builder()
                .webSocketFactory(OkHttpClientUtils.newWebSocketFactory(okClient, "ws://192.168.1.2:8080"))
                .addMessageAdapterFactory(new MoshiMessageAdapter.Factory())
                .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                .build().create(RCCarService.class);


        RCCommand command = new RCCommand("horn");
        command.putData("param", "start");
        rcCarService.sendCommand("HELLO");
    }

    private void setAppTheme() {
        SharedPreferences sharedPreferences = getSharedPreferences(ThemeDialogFragment.PREF_NAME, MODE_PRIVATE);
        switch (sharedPreferences.getInt(ThemeDialogFragment.PREF_KEY_APP_THEME, ThemeDialogFragment.DEFAULT_APP_THEME)) {
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
}
