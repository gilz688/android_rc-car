package ph.edu.msuiit.rccarclient.tcp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.models.ParcelableDevice;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsPresenter;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsView;
import ph.edu.msuiit.rccarclient.utils.ControlsSeekBar;

public class ControlsFragment extends Fragment implements ControlsView, ControlsSeekBar.OnSeekBarChangeListener, SensorEventListener{
    private static final String ARG_DEVICE = "device";
    private ParcelableDevice device;
    private ControlsPresenter mPresenter;

    private ControlsSeekBar verticalSeekBar;
    private ControlsSeekBar horizontalSeekBar;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int accelerometerValue = 0;

    private static final int MAXIMUM_SPEED = 255;
    private static final int MINIMUM_SPEED = -255;
    private static final int MAXIMUM_ANGLE = 70;
    private static final int MINIMUM_ANGLE = -70;

    public static ControlsFragment newInstance(ParcelableDevice device) {
        ControlsFragment fragment = new ControlsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_DEVICE, device);
        fragment.setArguments(args);
        return fragment;
    }

    public ControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            device = getArguments().getParcelable(ARG_DEVICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_controls,
                null);


        verticalSeekBar = (ControlsSeekBar) root.findViewById(R.id.vertical_seek_bar);
        verticalSeekBar.setMaxValue(MAXIMUM_SPEED);
        verticalSeekBar.setMinValue(MINIMUM_SPEED);
        verticalSeekBar.setProgressValue(0);
        verticalSeekBar.setOnSeekBarChangeListener(this);

        horizontalSeekBar = (ControlsSeekBar) root.findViewById(R.id.horizontal_seek_bar);
        horizontalSeekBar.setMaxValue(MAXIMUM_ANGLE);
        horizontalSeekBar.setMinValue(MINIMUM_ANGLE);
        horizontalSeekBar.setProgressValue(0);
        horizontalSeekBar.setOnSeekBarChangeListener(this);


        ToggleButton toggleAccelerometer = (ToggleButton) root.findViewById(R.id.toggle_accelerometer);
        toggleAccelerometer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    enableAccelerometer();
                } else {
                    disableAccelerometer();
                }
            }
        });

        mPresenter = new ControlsPresenterImpl(this, new ControlsInteractorImpl(getActivity()));
        mPresenter.onStart(device);

        return root;
    }
    private void enableAccelerometer() {
        accelerometerValue = 0;
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        horizontalSeekBar.disableTouchEvent();
    }

    private void disableAccelerometer() {
        accelerometerValue = 0;
        mSensorManager.unregisterListener(this);
        horizontalSeekBar.enableTouchEvent();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float yRaw;
        Sensor sensor = event.sensor;
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            WindowManager windowMgr = (WindowManager)getActivity().getSystemService(Context.WINDOW_SERVICE);
            int rotationIndex = windowMgr.getDefaultDisplay().getRotation();
            if (rotationIndex == 1 || rotationIndex == 3) { // detect 90 or 270 degree rotation
                yRaw = event.values[1];
            }
            else {
                yRaw = event.values[0];
            }
            accelerometerValue = accelerometerValue + Math.round(yRaw);
            if (accelerometerValue>MAXIMUM_ANGLE)
                accelerometerValue = MAXIMUM_ANGLE;
            if (accelerometerValue<MINIMUM_SPEED)
                accelerometerValue = MINIMUM_SPEED;
            horizontalSeekBar.setProgressValue(accelerometerValue);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.onStart(device);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onEnd();
        accelerometerValue = 0;
        disableAccelerometer();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch(seekBar.getId()) {
            case (R.id.vertical_seek_bar):
                mPresenter.onVSeekBarProgressChanged(progress);
                break;

            case (R.id.horizontal_seek_bar):
                mPresenter.onHSeekBarProgressChanged(progress);
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch(seekBar.getId()) {
            case (R.id.vertical_seek_bar):
                verticalSeekBar.setProgressValue(0);
                break;

            case (R.id.horizontal_seek_bar):
                horizontalSeekBar.setProgressValue(0);
                break;
        }
    }
}
