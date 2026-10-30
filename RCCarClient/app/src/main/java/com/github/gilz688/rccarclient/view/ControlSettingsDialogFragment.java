package com.github.gilz688.rccarclient.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.databinding.DialogControlsSettingsBinding;
import com.github.gilz688.rccarclient.model.ControlsConfig;
import com.github.gilz688.rccarclient.viewmodel.ControlsViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ControlSettingsDialogFragment extends BottomSheetDialogFragment {
    public static final String TAG = "ControlSettingsDialog";

    private DialogControlsSettingsBinding binding;
    private ControlsViewModel viewModel;

    public static ControlSettingsDialogFragment newInstance() {
        return new ControlSettingsDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogControlsSettingsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ControlsViewModel.class);

        ControlsConfig config = viewModel.getControlsConfig().getValue();
        if (config == null) {
            config = new ControlsConfig();
        }

        setupViews(config);
    }

    private void setupViews(@NonNull ControlsConfig config) {
        // Hand swap
        binding.switchHandSwap.setChecked(config.isHandSwap());
        updateHandSwapSubtitle(config.isHandSwap());
        binding.switchHandSwap.setOnCheckedChangeListener((btn, isChecked) -> {
            updateHandSwapSubtitle(isChecked);
            viewModel.setHandSwap(isChecked);
        });

        // Control Mode
        switch (config.getControlMode()) {
            case SEEKBAR:
                binding.radioModeSeekbar.setChecked(true);
                break;
            case ACCELEROMETER:
                binding.radioModeAccelerometer.setChecked(true);
                break;
            case JOYSTICK:
            default:
                binding.radioModeJoystick.setChecked(true);
                break;
        }

        binding.radioGroupControlMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_mode_seekbar) {
                viewModel.setControlMode(ControlsConfig.ControlMode.SEEKBAR);
            } else if (checkedId == R.id.radio_mode_accelerometer) {
                viewModel.setControlMode(ControlsConfig.ControlMode.ACCELEROMETER);
            } else {
                viewModel.setControlMode(ControlsConfig.ControlMode.JOYSTICK);
            }
        });

        // Invert Axes
        binding.switchInvertThrottle.setChecked(config.isInvertThrottle());
        binding.switchInvertThrottle.setOnCheckedChangeListener((btn, isChecked) ->
                viewModel.setInvertThrottle(isChecked));

        binding.switchInvertSteering.setChecked(config.isInvertSteering());
        binding.switchInvertSteering.setOnCheckedChangeListener((btn, isChecked) ->
                viewModel.setInvertSteering(isChecked));

        // Done button
        binding.buttonDone.setOnClickListener(v -> dismiss());
    }

    private void updateHandSwapSubtitle(boolean isSwapped) {
        if (isSwapped) {
            binding.textHandSwapSubtitle.setText("Left: Steering | Right: Throttle");
        } else {
            binding.textHandSwapSubtitle.setText("Left: Throttle | Right: Steering");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
