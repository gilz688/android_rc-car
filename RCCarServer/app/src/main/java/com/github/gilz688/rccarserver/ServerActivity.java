package com.github.gilz688.rccarserver;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ServerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ServerFragment())
                    .commit();
        }
    }
}
