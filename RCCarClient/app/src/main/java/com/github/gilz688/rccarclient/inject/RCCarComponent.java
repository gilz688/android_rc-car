package com.github.gilz688.rccarclient.inject;

import com.github.gilz688.rccarclient.interact.SensorInteractor;
import com.github.gilz688.rccarclient.viewmodel.ControlsViewModel;
import com.github.gilz688.rccarclient.viewmodel.DeviceDialogViewModel;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RCCarModule.class})
public
interface RCCarComponent {
    void inject(ControlsViewModel viewModel);
    void inject(DeviceDialogViewModel viewModel);
    void inject(SensorInteractor interactor);
}