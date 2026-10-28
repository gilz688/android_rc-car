package com.github.gilz688.rccarclient;
import androidx.multidex.MultiDexApplication;

import com.github.gilz688.rccarclient.inject.DaggerRCCarComponent;
import com.github.gilz688.rccarclient.inject.RCCarComponent;
import com.github.gilz688.rccarclient.inject.RCCarModule;

public class RCCarClientApplication extends MultiDexApplication {
    RCCarComponent component;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger%COMPONENT_NAME% is ang auto-generated class
        component = DaggerRCCarComponent.builder()
                .rCCarModule(new RCCarModule(this))
                .build();
    }

    public RCCarComponent getComponent() {
        return component;
    }
}
