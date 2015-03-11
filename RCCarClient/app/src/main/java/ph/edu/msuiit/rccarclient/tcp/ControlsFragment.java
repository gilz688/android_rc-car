package ph.edu.msuiit.rccarclient.tcp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.models.ParcelableDevice;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsPresenter;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsView;
import ph.edu.msuiit.rccarclient.utils.ControlsSeekBar;

public class ControlsFragment extends Fragment implements ControlsView, ControlsSeekBar.OnSeekBarChangeListener{
    private static final String ARG_DEVICE = "device";
    private ParcelableDevice device;
    private ControlsPresenter mPresenter;

    private ControlsSeekBar verticalSeekBar;
    private ControlsSeekBar horizontalSeekBar;

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
        verticalSeekBar.setMaxValue(255);
        verticalSeekBar.setMinValue(-255);
        verticalSeekBar.setProgressValue(0);
        verticalSeekBar.setOnSeekBarChangeListener(this);

        horizontalSeekBar = (ControlsSeekBar) root.findViewById(R.id.horizontal_seek_bar);
        horizontalSeekBar.setMaxValue(60);
        horizontalSeekBar.setMinValue(-60);
        horizontalSeekBar.setProgressValue(0);
        horizontalSeekBar.setOnSeekBarChangeListener(this);

        mPresenter = new ControlsPresenterImpl(this, new ControlsInteractorImpl(getActivity()));
        mPresenter.onStart(device);

        return root;
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
