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
import com.github.gilz688.rccarclient.model.ControlsConfig;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.viewmodel.ControlsViewModel;
import com.jmedeisis.bugstick.Joystick;
import com.jmedeisis.bugstick.JoystickListener;

public class ControlsFragment extends Fragment {
    private static final String TAG = "ControlsFragment";
    private FragmentControlsBinding binding;
    private ControlsViewModel viewModel;

    public ControlsFragment() {
        super(R.layout.fragment_controls);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inject the View Model (scoped to Activity so dialogs share the exact same instance)
        viewModel = new ViewModelProvider(requireActivity()).get(ControlsViewModel.class);

        // Configure the sensor manager and pass to the View Model
        RCCarComponent component = ((RCCarClientApplication) requireActivity().getApplication()).getComponent();
        Log.d(TAG, "inject()");
        viewModel.inject(component);
        viewModel.initPreferences(requireContext());

        // Initialize View Binding class
        binding = FragmentControlsBinding.bind(view);

        setupViews();
        setupObservers();
    }

    private void setupViews() {
        // Quick Action buttons
        binding.buttonSettings.setOnClickListener(v -> viewModel.onClickSettingsButton());
        binding.buttonTheme.setOnClickListener(v -> viewModel.onClickThemeButton());
        binding.buttonConnect.setOnClickListener(v -> viewModel.onClickConnectButton());

        // Quick Hand Swap toggle
        binding.buttonQuickSwap.setOnClickListener(v -> {
            ControlsConfig cfg = viewModel.getControlsConfig().getValue();
            boolean newSwap = (cfg == null || !cfg.isHandSwap());
            viewModel.setHandSwap(newSwap);
        });

        // Accelerometer toggle
        binding.toggleAccelerometer.setOnClickListener(v -> viewModel.onClickAccelerometerButton());

        // Horn with touch listener for press and release
        binding.buttonHorn.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    viewModel.onPressHornButton();
                    binding.buttonHorn.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.cockpit_amber)));
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    viewModel.onReleaseHornButton();
                    binding.buttonHorn.setBackgroundTintList(ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), R.color.cockpit_red)));
                    break;
            }
            return false;
        });

        // Left Joystick: default Throttle (locked to vertical axis), swappable to Steering (horizontal axis)
        binding.leftJoystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
                viewModel.vibrate();
            }

            @Override
            public void onDrag(float degrees, float offset) {
                ControlsConfig cfg = viewModel.getControlsConfig().getValue();
                boolean swapped = (cfg != null && cfg.isHandSwap());
                if (swapped) {
                    // Left controls Steering (Horizontal axis)
                    // 0° = right (+), 180° = left (-)
                    float normalized = (float) (offset * Math.cos(Math.toRadians(degrees)));
                    viewModel.onSteeringInput(Math.round(normalized * ControlsViewModel.MAXIMUM_ANGLE));
                } else {
                    // Left controls Throttle (Vertical axis)
                    // +90° = up (+ forward), -90° = down (- reverse)
                    float normalized = (float) (offset * Math.sin(Math.toRadians(degrees)));
                    viewModel.onThrottleInput(Math.round(normalized * ControlsViewModel.MAXIMUM_SPEED));
                }
            }

            @Override
            public void onUp() {
                ControlsConfig cfg = viewModel.getControlsConfig().getValue();
                boolean swapped = (cfg != null && cfg.isHandSwap());
                if (swapped) {
                    viewModel.onSteeringInput(0);
                } else {
                    viewModel.onThrottleInput(0);
                }
            }
        });

        // Right Joystick: default Steering (locked to horizontal axis), swappable to Throttle (vertical axis)
        binding.rightJoystick.setJoystickListener(new JoystickListener() {
            @Override
            public void onDown() {
                viewModel.vibrate();
            }

            @Override
            public void onDrag(float degrees, float offset) {
                ControlsConfig cfg = viewModel.getControlsConfig().getValue();
                boolean swapped = (cfg != null && cfg.isHandSwap());
                if (swapped) {
                    // Right controls Throttle (Vertical axis)
                    // +90° = up (+ forward), -90° = down (- reverse)
                    float normalized = (float) (offset * Math.sin(Math.toRadians(degrees)));
                    viewModel.onThrottleInput(Math.round(normalized * ControlsViewModel.MAXIMUM_SPEED));
                } else {
                    // Right controls Steering (Horizontal axis)
                    // 0° = right (+), 180° = left (-)
                    float normalized = (float) (offset * Math.cos(Math.toRadians(degrees)));
                    viewModel.onSteeringInput(Math.round(normalized * ControlsViewModel.MAXIMUM_ANGLE));
                }
            }

            @Override
            public void onUp() {
                ControlsConfig cfg = viewModel.getControlsConfig().getValue();
                boolean swapped = (cfg != null && cfg.isHandSwap());
                if (swapped) {
                    viewModel.onThrottleInput(0);
                } else {
                    viewModel.onSteeringInput(0);
                }
            }
        });
    }

    private void setupObservers() {
        LifecycleOwner viewLifecycleOwner = getViewLifecycleOwner();

        // Observe controls configuration changes
        viewModel.getControlsConfig().observe(viewLifecycleOwner, this::updateControlsUi);

        // Observe changes in device
        viewModel.getDeviceData().observe(viewLifecycleOwner, (Device device) -> {
            if (device == null) {
                binding.textConnectionStatus.setText(R.string.status_disconnected);
                binding.statusLed.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.gray)));
                showToast("Disconnected from vehicle.");
            } else {
                String label = device.getName() != null && !device.getName().isEmpty()
                        ? device.getName()
                        : getString(R.string.status_connected);
                binding.textConnectionStatus.setText(label);
                binding.statusLed.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(requireContext(), R.color.cockpit_green)));
                showToast("Connected to " + label + ".");
            }
        });

        // Observe connection state
        viewModel.getConnectedData().observe(viewLifecycleOwner, (Boolean isConnected) -> {
            int connectTint = isConnected
                    ? ContextCompat.getColor(requireContext(), R.color.cockpit_green)
                    : ContextCompat.getColor(requireContext(), R.color.cockpit_cyan);
            binding.buttonConnect.setBackgroundTintList(ColorStateList.valueOf(connectTint));
        });

        // Observe events from view model
        viewModel.getActionData().observe(viewLifecycleOwner, event -> {
            ControlsViewModel.Action action = event.getContentIfNotHandledOrReturnNull();
            if (action != null) {
                switch (action) {
                    case SHOW_CONTROL_SETTINGS_DIALOG:
                        ControlSettingsDialogFragment.newInstance().show(
                                requireActivity().getSupportFragmentManager(), ControlSettingsDialogFragment.TAG);
                        break;
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
                        break;
                }
            }
        });

        // Observe tilt / accelerometer sensor reading
        viewModel.getCurrentSensorReading().observe(viewLifecycleOwner, (Integer value) -> {
            if (value != null) {
                viewModel.onSteeringInput(value);
            }
        });
    }

    private void updateControlsUi(@NonNull ControlsConfig config) {
        boolean swapped = config.isHandSwap();
        if (swapped) {
            // Left is Steering (Horizontal axis locked)
            binding.leftJoystick.setMotionConstraint(Joystick.MotionConstraint.HORIZONTAL);
            binding.leftJoystick.setBackgroundResource(R.drawable.joystick_track_horizontal);
            binding.textLeftZoneLabel.setText(R.string.controls_steering_label);
            binding.textLeftZoneLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.cockpit_cyan));

            // Right is Throttle (Vertical axis locked)
            binding.rightJoystick.setMotionConstraint(Joystick.MotionConstraint.VERTICAL);
            binding.rightJoystick.setBackgroundResource(R.drawable.joystick_track_vertical);
            binding.textRightZoneLabel.setText(R.string.controls_throttle_label);
            binding.textRightZoneLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.cockpit_amber));

            binding.textControlModeBadge.setText("L: STEER (↔) | R: THROTTLE (↕)");
            binding.buttonQuickSwap.setText("SWAPPED");
            binding.buttonQuickSwap.setIconTint(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.cockpit_cyan)));
        } else {
            // Left is Throttle (Vertical axis locked)
            binding.leftJoystick.setMotionConstraint(Joystick.MotionConstraint.VERTICAL);
            binding.leftJoystick.setBackgroundResource(R.drawable.joystick_track_vertical);
            binding.textLeftZoneLabel.setText(R.string.controls_throttle_label);
            binding.textLeftZoneLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.cockpit_amber));

            // Right is Steering (Horizontal axis locked)
            binding.rightJoystick.setMotionConstraint(Joystick.MotionConstraint.HORIZONTAL);
            binding.rightJoystick.setBackgroundResource(R.drawable.joystick_track_horizontal);
            binding.textRightZoneLabel.setText(R.string.controls_steering_label);
            binding.textRightZoneLabel.setTextColor(ContextCompat.getColor(requireContext(), R.color.cockpit_cyan));

            binding.textControlModeBadge.setText("L: THROTTLE (↕) | R: STEER (↔)");
            binding.buttonQuickSwap.setText("SWAP");
            binding.buttonQuickSwap.setIconTint(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.cockpit_amber)));
        }

        binding.leftJoystick.requestLayout();
        binding.leftJoystick.invalidate();
        binding.rightJoystick.requestLayout();
        binding.rightJoystick.invalidate();

        if (config.getControlMode() == ControlsConfig.ControlMode.ACCELEROMETER) {
            binding.toggleAccelerometer.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.cockpit_cyan)));
            binding.toggleAccelerometer.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
        } else {
            binding.toggleAccelerometer.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(requireContext(), R.color.cockpit_surface)));
            binding.toggleAccelerometer.setTextColor(ContextCompat.getColor(requireContext(), R.color.cockpit_text_primary));
        }
    }

    private void vibrate() {
        long vibrateTime = 50L;
        Vibrator vibrator = (Vibrator) requireContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(vibrateTime, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(vibrateTime);
            }
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 80);
        toast.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
