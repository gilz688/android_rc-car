package com.github.gilz688.rccarclient.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Configuration model for customizable RC Car controls.
 */
public class ControlsConfig implements Serializable {
    public enum ControlMode {
        JOYSTICK,
        SEEKBAR,
        ACCELEROMETER
    }

    private boolean handSwap; // false = Left: Throttle, Right: Steering. true = Left: Steering, Right: Throttle.
    private ControlMode controlMode;
    private boolean invertThrottle;
    private boolean invertSteering;

    public ControlsConfig() {
        this(false, ControlMode.JOYSTICK, false, false);
    }

    public ControlsConfig(boolean handSwap, ControlMode controlMode, boolean invertThrottle, boolean invertSteering) {
        this.handSwap = handSwap;
        this.controlMode = controlMode != null ? controlMode : ControlMode.JOYSTICK;
        this.invertThrottle = invertThrottle;
        this.invertSteering = invertSteering;
    }

    public boolean isHandSwap() {
        return handSwap;
    }

    public void setHandSwap(boolean handSwap) {
        this.handSwap = handSwap;
    }

    public ControlMode getControlMode() {
        return controlMode;
    }

    public void setControlMode(ControlMode controlMode) {
        this.controlMode = controlMode != null ? controlMode : ControlMode.JOYSTICK;
    }

    public boolean isInvertThrottle() {
        return invertThrottle;
    }

    public void setInvertThrottle(boolean invertThrottle) {
        this.invertThrottle = invertThrottle;
    }

    public boolean isInvertSteering() {
        return invertSteering;
    }

    public void setInvertSteering(boolean invertSteering) {
        this.invertSteering = invertSteering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ControlsConfig that = (ControlsConfig) o;
        return handSwap == that.handSwap &&
                invertThrottle == that.invertThrottle &&
                invertSteering == that.invertSteering &&
                controlMode == that.controlMode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(handSwap, controlMode, invertThrottle, invertSteering);
    }
}
