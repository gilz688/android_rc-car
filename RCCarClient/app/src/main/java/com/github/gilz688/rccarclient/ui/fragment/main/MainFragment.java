 package com.github.gilz688.rccarclient.ui.fragment.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.databinding.FragmentControlsBinding;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.ui.SharedViewModel;
import com.github.gilz688.rccarclient.ui.dialogs.device.DeviceDialog;
import com.github.gilz688.rccarclient.ui.dialogs.theme.ThemeDialog;
import com.github.gilz688.rccarclient.widget.ControlsSeekBar;

public class MainFragment extends Fragment implements ControlsSeekBar.OnSeekBarChangeListener, DeviceDialog.DeviceDialogListener {
    private FragmentControlsBinding binding;
    private SharedViewModel sharedViewModel;
    private MainViewModel viewModel;

    public MainFragment() {
        super(R.layout.fragment_controls);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inject the View Models
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Configure the sensor manager and pass to the View Model
        viewModel.setSensorManager((SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE));

        // Initialize View Binding class
        binding = FragmentControlsBinding.bind(view);

        setupViews();
        setupObservers();
    }

    private void setupViews() {
        // Configure pass button click events to ViewModel
        binding.buttonTheme.setOnClickListener((button) -> viewModel.onClickThemeButton());
        binding.buttonConnect.setOnClickListener((button) -> viewModel.onClickConnectButton());
        binding.toggleAccelerometer.setOnClickListener((button) -> viewModel.onClickAccelerometerButton());
        binding.buttonHorn.setOnTouchListener((v, event) -> {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    sharedViewModel.startHorn();
                    viewModel.vibrate();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    sharedViewModel.stopHorn();
                    break;
            }
            return false;
        });

        // Configure accelerate joystick
        ControlsSeekBar verticalSeekBar = binding.verticalSeekBar;
        verticalSeekBar.setMinValue(MainViewModel.MINIMUM_SPEED);
        verticalSeekBar.setMaxValue(MainViewModel.MAXIMUM_SPEED);
        verticalSeekBar.setProgressValue(0);
        verticalSeekBar.setOnSeekBarChangeListener(this);

        // Configure steering joystick
        ControlsSeekBar horizontalSeekBar2 = binding.horizontalSeekBar2;
        horizontalSeekBar2.setMinValue(MainViewModel.MINIMUM_ANGLE);
        horizontalSeekBar2.setMaxValue(MainViewModel.MAXIMUM_ANGLE);
        horizontalSeekBar2.setProgressValue(0);
        horizontalSeekBar2.setOnSeekBarChangeListener(this);
    }

    private void setupObservers() {
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();
        // Observe changes to connection from sharedViewModel
        sharedViewModel.getConnectedData().observe(viewLifecycleOwner, connected -> {
            if(connected) {
                viewModel.onDeviceConnected();
            } else {
                viewModel.onDeviceDisconnected();
            }
        });

        // Observe changes in connect button color
        viewModel.getFabConnectColor().observe(viewLifecycleOwner, value -> {
            binding.buttonConnect.setBackgroundTintList(value != 0 ? ColorStateList.valueOf(ContextCompat.getColor(binding.buttonConnect.getContext(), value)) : null);
        });

        // Observe changes in accelerometer button color
        viewModel.getFabAccelerometerColor().observe(viewLifecycleOwner, value -> {
            binding.toggleAccelerometer.setBackgroundTintList(value != 0 ? ColorStateList.valueOf(ContextCompat.getColor(binding.toggleAccelerometer.getContext(), value)) : null);
        });

        // Observe when theme dialog is requested
        viewModel.getReqAppTheme().observe(viewLifecycleOwner, value -> {
            if(value) {
                ThemeDialog themeDialog = new ThemeDialog();
                themeDialog.show(requireActivity().getSupportFragmentManager(), ThemeDialog.TAG);
                viewModel.getReqAppTheme().postValue(false);
            }
        });

        // Observe when device dialog is requested
        viewModel.getReqConnect().observe(viewLifecycleOwner, connect -> {
            if(connect) {
                if(sharedViewModel.getConnectedData().getValue()) {
                    sharedViewModel.disconnectDevice();
                } else {
                    DeviceDialog deviceDialog = new DeviceDialog(this);
                    deviceDialog.show(requireActivity().getSupportFragmentManager(), DeviceDialog.TAG);
                }
            }
            viewModel.getReqConnect().postValue(false);
        });

        // Observe when horizontal value is updated from the View Model
        viewModel.getHorizontalValue().observe(viewLifecycleOwner, (value) -> {
            binding.horizontalSeekBar2.setProgressValue(value);
            // Log.d("MainFragment", "getHorizontalValue: " + value);
            sharedViewModel.setAngle(value);
        });

        // Observe when vertical value is updated from the View Model
        viewModel.getVerticalValue().observe(viewLifecycleOwner, (value) -> {
            // Log.d("MainFragment", "getVerticalValue: " + value);
            sharedViewModel.setSpeed(value);
        });

        // Observe when accelerometer is enabled or disabled
        viewModel.getEnHorizontalJoystick().observe(viewLifecycleOwner, (value) -> {
            if(value == null) return;

            if(value) {
                binding.horizontalSeekBar2.disableTouchEvent();
            } else {
                viewModel.getHorizontalValue().postValue(0);
                binding.horizontalSeekBar2.setProgressValue(0);
                binding.horizontalSeekBar2.enableTouchEvent();
            }
        });

        // Observe when vibration is requested
        viewModel.getVibrate().observe(getViewLifecycleOwner(), (value) -> {
            if(value) {
                vibrate();
                viewModel.getVibrate().setValue(false);
            }
        });
    }

    private void vibrate() {
        long vibrateTime = 100L;
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(vibrateTime, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(vibrateTime);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case (R.id.vertical_seek_bar):
                viewModel.getVerticalValue().postValue(progress);
                break;

            case (R.id.horizontal_seek_bar_2):
                viewModel.getHorizontalValue().postValue(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case (R.id.vertical_seek_bar):
                binding.verticalSeekBar.setProgressValue(0);
                break;

            case (R.id.horizontal_seek_bar_2):
                binding.horizontalSeekBar2.setProgressValue(0);
                break;
        }
    }

    @Override
    public void onDeviceSelected(Device device) {
        sharedViewModel.setDevice(device);
    }
}
