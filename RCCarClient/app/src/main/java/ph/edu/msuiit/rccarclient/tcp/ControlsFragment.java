package ph.edu.msuiit.rccarclient.tcp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.discovery.DiscoveryBroadcastReceiver;
import ph.edu.msuiit.rccarclient.discovery.DiscoveryService;
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
    private Sensor mAccelerometer, mMagnetometer;

    float matrixR[] = null; //for gravity rotational data
    float matrixI[] = null; //for magnetic rotational data
    float accelerometerValues[] = new float[3];
    float magnetometerValues[] = new float[3];
    float[] angle = new float[3];

    private static final int MAXIMUM_SPEED = 3;
    private static final int MINIMUM_SPEED = -3;
    private static final int MAXIMUM_ANGLE = 3;
    private static final int MINIMUM_ANGLE = -3;

    // handler for received Intents for the "tcp" event
    BroadcastReceiver mMessageReceiver;

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
        mMessageReceiver = new TCPBroadcastReceiver(mPresenter);
        root.setBackgroundColor(getResources().getColor(R.color.background));

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

        Button btnHorn = (Button) root.findViewById(R.id.button_horn);
        btnHorn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mPresenter.onHornButtonTouched();
                        return true;
                    case MotionEvent.ACTION_UP:
                        mPresenter.onHornButtonReleased();
                        return true;
                }
                return false;
            }
        });

        return root;
    }
    private void enableAccelerometer() {
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mMagnetometer,SensorManager.SENSOR_DELAY_NORMAL);
        horizontalSeekBar.disableTouchEvent();
    }

    private void disableAccelerometer() {
        if(mSensorManager != null)
            mSensorManager.unregisterListener(this);
        horizontalSeekBar.enableTouchEvent();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType())
        {
            case Sensor.TYPE_MAGNETIC_FIELD:
                magnetometerValues = lowPass(event.values.clone(), magnetometerValues);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = lowPass(event.values.clone(), magnetometerValues);
                break;
        }

        if (magnetometerValues != null && accelerometerValues != null) {
            matrixR = new float[9];
            matrixI = new float[9];

            SensorManager.getRotationMatrix(matrixR, matrixI, accelerometerValues, magnetometerValues);

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(matrixR, SensorManager.AXIS_X,SensorManager.AXIS_MINUS_Y, outR);
            SensorManager.getOrientation(outR, angle);

            float pitch = angle[1] * 57.2957795f;

            magnetometerValues = null; // retrigger the loop when things are repopulated
            accelerometerValues = null; // retrigger the loop when things are repopulated

            int value = map(pitch, -20 , 20, MINIMUM_ANGLE, MAXIMUM_ANGLE);

            horizontalSeekBar.setProgressValue(value);
        }
    }

    public int map(float value, float in_min, float in_max, float out_min, float out_max){
        return Math.round((value - in_min) * (out_max - out_min) / (in_max - in_min) + out_min);
    }

    final float ALPHA = 0.5f;

    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceivers();
        mPresenter.onStart(device);
    }

    @Override
    public void onPause() {
        super.onPause();
        unRegisterReceivers();
        mPresenter.onEnd();
        disableAccelerometer();
    }

    public void registerReceivers(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(TCPService.ACTION_TCP_CONNECTED);
        filter.addAction(TCPService.ACTION_TCP_DISCONNECTED);
        filter.addAction(TCPService.ACTION_TCP_RESPONSE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                filter);
    }

    public void unRegisterReceivers() {
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mMessageReceiver);
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

    @Override
    public void closeView() {
        getActivity().finish();
    }
}
