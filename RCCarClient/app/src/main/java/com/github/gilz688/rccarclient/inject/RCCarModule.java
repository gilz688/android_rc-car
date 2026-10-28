package com.github.gilz688.rccarclient.inject;

import android.app.Application;
import android.content.Context;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;

import com.github.gilz688.rccarclient.api.RCCarService;
import com.tinder.scarlet.Scarlet;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.content.Context.SENSOR_SERVICE;

@Module
public class RCCarModule {
    private final Application application;
    private final WifiManager wifiManager;
    private final SensorManager sensorManager;

    public RCCarModule(Application application) {
        this.application = application;
        sensorManager = (SensorManager) application.getSystemService(SENSOR_SERVICE);
        wifiManager = (WifiManager) application.getSystemService(Context.WIFI_SERVICE);
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return application;
    }

    @Provides
    @Singleton
    WifiManager providesWifiManager() {
        return wifiManager;
    }

    @Provides
    @Singleton
    SensorManager providesSensorManager() {
        return sensorManager;
    }
}