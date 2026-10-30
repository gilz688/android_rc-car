# Specification: Modern Dark Cockpit Material 3 UI & Swappable Controls

- **Project**: `android_rc-car` (`RCCarClient`)
- **Status**: Verified
- **Created**: 2026-10-30

## 1. Problem Statement
The current `RCCarClient` UI uses legacy Material Design floating action buttons and fixed SeekBars/joysticks without customization. Drivers have different preferences (left-handed throttle vs right-handed throttle, joystick vs slider seekbar, tilt vs manual steering). The visual design lacks immersion for an RC vehicle cockpit.

## 2. Technical Scope
- **Theme**: Upgrade to Material 3 Day/Night with a tactical Dark Cockpit theme (`Theme.Material3.DayNight.NoActionBar` base, `#0B0F19` deep obsidian background, neon cyan `#00E5FF` primary, racing amber `#FF9100` warning/status).
- **Controls Layout**: Retain dual-zone landscape ergonomics while allowing dynamic swapping of left/right sticks and selectable input modalities.
- **Components to Modify**:
  - `res/values/themes.xml`, `res/values-night/themes.xml`, `res/values/colors.xml`: Material 3 cockpit color palette.
  - `res/layout/fragment_controls.xml`: Ergonomic cockpit HUD layout with left/right swappable stick containers, telemetry status bar, and quick settings button.
  - `res/layout/dialog_control_settings.xml`: Bottom sheet or dialog for on-the-fly control customization (hand swap, control mode, axis invert).
  - `com.github.gilz688.rccarclient.model.ControlsConfig`: Configuration model for control assignments.
  - `com.github.gilz688.rccarclient.util.ControlsPreferences`: SharedPreferences persistence.
  - `com.github.gilz688.rccarclient.viewmodel.ControlsViewModel`: Expose dynamic control mappings and reactive state.
  - `com.github.gilz688.rccarclient.view.ControlsFragment`: Bind swappable controls and settings trigger.

## 3. Acceptance Criteria (AC)
- **AC-1 (Dark Cockpit Material 3 Theme)**: The app adopts a dark cockpit visual aesthetic adhering to Material 3 tokens with crisp contrast, rounded surfaces, and clear visual hierarchy.
- **AC-2 (Ergonomic Layout Preservation)**: Primary driving controls remain positioned in left and right thumb reachable zones in landscape orientation.
- **AC-3 (Swappable Hand Orientation)**: User can swap Throttle (Forward/Reverse) and Steering (Left/Right) between the left and right zones.
- **AC-4 (Selectable Control Modes)**:
  - Mode 1: Dual Joysticks (Bugstick/Custom joystick with spring center).
  - Mode 2: Sliders / SeekBars (`ControlsSeekBar` vertical throttle + horizontal steering).
  - Mode 3: Gyro/Accelerometer steering with single throttle stick.
- **AC-5 (Axis Inversion & Safety)**: User can invert steering axis (Normal vs Inverted) and throttle axis (Normal vs Inverted).
- **AC-6 (Settings Persistence)**: Control configurations persist across app sessions via `SharedPreferences`.
- **AC-7 (Telemetry & Status HUD)**: Status header displays vehicle connection state, IP/name, and active control mode badge.

## 4. Verification Plan
- Build verification: `./gradlew assembleDebug` in `RCCarClient`.
- Unit test verification: `./gradlew testDebugUnitTest` verifying `ControlsConfig`, `ControlsPreferences`, and `ControlsViewModel`.
