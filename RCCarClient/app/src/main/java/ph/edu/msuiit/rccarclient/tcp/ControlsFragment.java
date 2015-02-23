package ph.edu.msuiit.rccarclient.tcp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.models.ParcelableDevice;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsPresenter;
import ph.edu.msuiit.rccarclient.tcp.proto.ControlsView;

public class ControlsFragment extends Fragment implements ControlsView, View.OnClickListener {
    private static final String ARG_DEVICE = "device";
    private ParcelableDevice device;
    private ControlsPresenterImpl mPresenter;

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

        root.findViewById(R.id.btnLeft).setOnClickListener(this);
        root.findViewById(R.id.btnRight).setOnClickListener(this);
        root.findViewById(R.id.btnForward).setOnClickListener(this);
        root.findViewById(R.id.btnBackward).setOnClickListener(this);

        mPresenter = new ControlsPresenterImpl(this, new ControlsInteractorImpl());
        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnForward:
                mPresenter.onForwardButtonClick();
                break;
            case R.id.btnBackward:
                mPresenter.onBackwardButtonClick();
                break;
            case R.id.btnLeft:
                mPresenter.onLeftButtonClick();
                break;
            case R.id.btnRight:
                mPresenter.onRightButtonClick();
                break;
            default:
        }
    }
}
