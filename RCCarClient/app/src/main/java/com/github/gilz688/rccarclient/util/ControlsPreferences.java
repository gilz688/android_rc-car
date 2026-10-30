package com.github.gilz688.rccarclient.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.github.gilz688.rccarclient.model.ControlsConfig;

/**
 * Persists and retrieves control preferences for RC Car Client.
 */
public class ControlsPreferences {
    private static final String PREF_NAME = "rccar_controls_prefs";
    private static final String KEY_HAND_SWAP = "controls_hand_swap";
    private static final String KEY_CONTROL_MODE = "controls_mode";
    private static final String KEY_INVERT_THROTTLE = "controls_invert_throttle";
    private static final String KEY_INVERT_STEERING = "controls_invert_steering";

    private final SharedPreferences prefs;

    public ControlsPreferences(@NonNull Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @NonNull
    public ControlsConfig loadConfig() {
        boolean handSwap = prefs.getBoolean(KEY_HAND_SWAP, false);
        String modeName = prefs.getString(KEY_CONTROL_MODE, ControlsConfig.ControlMode.JOYSTICK.name());
        ControlsConfig.ControlMode mode;
        try {
            mode = ControlsConfig.ControlMode.valueOf(modeName);
        } catch (Exception e) {
            mode = ControlsConfig.ControlMode.JOYSTICK;
        }
        boolean invertThrottle = prefs.getBoolean(KEY_INVERT_THROTTLE, false);
        boolean invertSteering = prefs.getBoolean(KEY_INVERT_STEERING, false);
        return new ControlsConfig(handSwap, mode, invertThrottle, invertSteering);
    }

    public void saveConfig(@NonNull ControlsConfig config) {
        prefs.edit()
                .putBoolean(KEY_HAND_SWAP, config.isHandSwap())
                .putString(KEY_CONTROL_MODE, config.getControlMode().name())
                .putBoolean(KEY_INVERT_THROTTLE, config.isInvertThrottle())
                .putBoolean(KEY_INVERT_STEERING, config.isInvertSteering())
                .apply();
    }
}
