 package com.github.gilz688.rccarclient.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.RCCarClientApplication;
import com.github.gilz688.rccarclient.databinding.FragmentControlsBinding;
import com.github.gilz688.rccarclient.inject.RCCarComponent;
import com.github.gilz688.rccarclient.widget.ControlsSeekBar;
import com.github.gilz688.rccarclient.viewmodel.ControlsViewModel;
import com.jmedeisis.bugstick.JoystickListener;

 public class ControlsFragment extends Fragment {
    private FragmentControlsBinding binding;
    private ControlsViewModel viewModel;

    public ControlsFragment() {
        super(R.layout.fragment_controls);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inject the View Model
        viewModel = new ViewModelProvider(this).get(ControlsViewModel.class);

        // Configure the sensor manager and pass to the View Model
        RCCarComponent component = ((RCCarClientApplication) requireActivity().getApplication()).getComponent();
        Log.d("ControlsFragment", "inject()");
        viewModel.inject(component);

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
                    viewModel.onPressHornButton();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    viewModel.onReleaseHornButton();
                    break;
            }
            return false;
        });
    }

    private void setupObservers() {
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();

        // Observe changes in device
        viewModel.getDeviceData().observe(viewLifecycleOwner, value -> {
            if(value == null) {
                showToast("Disconnected from " + value.getName() + ".");
            } else {
                showToast("Connected to " + value.getName() + ".");
            }
        });

        // Observe changes in connect button color
        viewModel.getFabConnectColorData().observe(viewLifecycleOwner, value -> {
            binding.buttonConnect.setBackgroundTintList(value != 0 ? ColorStateList.valueOf(ContextCompat.getColor(binding.buttonConnect.getContext(), value)) : null);
        });

        // Observe changes in accelerometer button color
        viewModel.getFabAccelerometerColor().observe(viewLifecycleOwner, value -> {
            binding.toggleAccelerometer.setBackgroundTintList(value != 0 ? ColorStateList.valueOf(ContextCompat.getColor(binding.toggleAccelerometer.getContext(), value)) : null);
        });

        // Observe events from view model
        viewModel.getActionData().observe(viewLifecycleOwner, event -> {
            ControlsViewModel.Action action = event.getContentIfNotHandledOrReturnNull();
            if(action != null) {
                switch (action) {
                    case SHOW_THEME_DIALOG:
                        ThemeDialogFragment themeDialog = new ThemeDialogFragment();
                        themeDialog.show(requireActivity().getSupportFragmentManager(), ThemeDialogFragment.TAG);
                        break;
                    case SHOW_CONNECT_DIALOG:
                        DeviceDialogFragment deviceDialog = new DeviceDialogFragment(viewModel);
                        deviceDialog.show(requireActivity().getSupportFragmentManager(), DeviceDialogFragment.TAG);
                        break;
                    case VIBRATE:
                        vibrate();
                    default:
                }
            }
        });

        // Observe when accelerometer is enabled or disabled
        viewModel.getEnHorizontalJoystick().observe(viewLifecycleOwner, (value) -> {
            if(value == null) return;

        });

        // Observe reading from sensor is received
        viewModel.getCurrentSensorReading().observe(getViewLifecycleOwner(), (value) -> {
            //binding.horizontalJoystick.setProgressValue(value);
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

    private void showToast(String message) {
        Toast toast = Toast.makeText(requireContext(),
                message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
}
