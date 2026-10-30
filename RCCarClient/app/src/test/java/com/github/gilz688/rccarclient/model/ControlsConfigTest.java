package com.github.gilz688.rccarclient.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class ControlsConfigTest {

    @Test
    public void testDefaultConfig() {
        ControlsConfig config = new ControlsConfig();
        assertFalse(config.isHandSwap());
        assertEquals(ControlsConfig.ControlMode.JOYSTICK, config.getControlMode());
        assertFalse(config.isInvertThrottle());
        assertFalse(config.isInvertSteering());
    }

    @Test
    public void testCustomConfig() {
        ControlsConfig config = new ControlsConfig(true, ControlsConfig.ControlMode.ACCELEROMETER, true, true);
        assertTrue(config.isHandSwap());
        assertEquals(ControlsConfig.ControlMode.ACCELEROMETER, config.getControlMode());
        assertTrue(config.isInvertThrottle());
        assertTrue(config.isInvertSteering());
    }

    @Test
    public void testSettersAndEquality() {
        ControlsConfig config1 = new ControlsConfig();
        ControlsConfig config2 = new ControlsConfig();
        assertEquals(config1, config2);

        config1.setHandSwap(true);
        config1.setControlMode(ControlsConfig.ControlMode.SEEKBAR);
        config1.setInvertThrottle(true);
        config1.setInvertSteering(true);

        assertNotEquals(config1, config2);

        config2.setHandSwap(true);
        config2.setControlMode(ControlsConfig.ControlMode.SEEKBAR);
        config2.setInvertThrottle(true);
        config2.setInvertSteering(true);

        assertEquals(config1, config2);
        assertEquals(config1.hashCode(), config2.hashCode());
    }
}
