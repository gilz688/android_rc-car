package ph.edu.msuiit.rccarclient.discovery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.adapters.DeviceAdapter;
import ph.edu.msuiit.rccarclient.models.Device;

/**
 * A fragment representing a list of RC Car Servers.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link ph.edu.msuiit.rccarclient.discovery.DiscoveryFragment.OnStatusUpdateListener}
 * interface.
 */
public class DiscoveryFragment extends Fragment implements DiscoveryView{
    private OnStatusUpdateListener mListener;
    private RecyclerView rvDevice;
    private DeviceAdapter mAdapter;
    private DiscoveryPresenter mPresenter;

    // handler for received Intents for the "server_discovery" event
    BroadcastReceiver mMessageReceiver;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DiscoveryFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_device,
                null);

        rvDevice = (RecyclerView) root.findViewById(R.id.rvDevice);
        rvDevice.setHasFixedSize(true);
        mAdapter = new DeviceAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvDevice.setLayoutManager(llm);
        rvDevice.setAdapter(mAdapter);

        mPresenter = new DiscoveryPresenterImpl(this);
        mMessageReceiver = new DiscoveryBroadcastReceiver(mPresenter);
        startDiscovery();

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if(mPresenter != null)
                    mPresenter.onRefreshButtonClicked();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnStatusUpdateListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void showProgress() {
        mListener.onStatusUpdate(OnStatusUpdateListener.STATUS.BUSY);
    }

    @Override
    public void hideProgress() {
        mListener.onStatusUpdate(OnStatusUpdateListener.STATUS.READY);
    }

    @Override
    public void addDevice(Device device) {
        if(mAdapter != null)
            mAdapter.add(device);
    }

    @Override
    public void refreshDeviceList() {
        mAdapter.setEmpty();
        startDiscovery();
    }

    public void startDiscovery(){
        Activity activity = getActivity();
        Intent intent = new Intent(activity, DiscoveryService.class);
        intent.setAction(DiscoveryService.INTENT_START_DISCOVERY);
        activity.startService(intent);
    }

    public void registerDiscoveryReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_SERVER_FOUND);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_CLIENT_STARTED);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_CLIENT_ENDED);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_CLIENT_ERROR);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                filter);
    }

    public void unRegisterDiscoveryReceiver() {
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        registerDiscoveryReceiver();
        if(mPresenter != null)
            mPresenter.onStart();
        super.onResume();
    }

    @Override
    public void onPause() {
        unRegisterDiscoveryReceiver();
        if(mPresenter != null)
            mPresenter.onEnd();
        super.onPause();
    }
}
