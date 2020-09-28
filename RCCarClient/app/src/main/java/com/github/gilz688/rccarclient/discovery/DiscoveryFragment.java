package com.github.gilz688.rccarclient.discovery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.adapters.DeviceAdapter;
import com.github.gilz688.rccarclient.discovery.proto.DiscoveryPresenter;
import com.github.gilz688.rccarclient.discovery.proto.DiscoveryView;
import com.github.gilz688.rccarclient.models.Device;
import com.github.gilz688.rccarclient.models.ParcelableDevice;

/**
 * A fragment representing a list of RC Car Servers.
 * <p/>
 * <p/>
 * Activities containing this fragment MUST implement the {@link com.github.gilz688.rccarclient.discovery.DiscoveryFragment.OnStatusUpdateListener}
 * interface.
 */
public class DiscoveryFragment extends Fragment implements DiscoveryView, DeviceAdapter.OnItemClickListener{
    private final String STATE_DEVICE_LIST = "STATE_DEVICE_LIST";
    private final String STATE_IS_ERROR_VISIBLE = "STATE_IS_ERROR_VISIBLE";
    private final String STATE_ERROR_MESSAGE = "STATE_ERROR_MESSAGE";

    private OnStatusUpdateListener mListener;
    private RecyclerView rvDevice;
    private DeviceAdapter mAdapter;
    private DiscoveryPresenter mPresenter;
    private View errorView;
    private TextView tvErrorMessage;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_device,
                null);
        errorView = root.findViewById(R.id.llError);
        tvErrorMessage = (TextView) root.findViewById(R.id.tvErrorMessage);

        rvDevice = (RecyclerView) root.findViewById(R.id.rvDevice);
        rvDevice.setHasFixedSize(true);
        mAdapter = new DeviceAdapter();
        LinearLayoutManager llm = new LinearLayoutManager(root.getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvDevice.setLayoutManager(llm);
        rvDevice.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

        mPresenter = new DiscoveryPresenterImpl(this, new DiscoveryInteractorImpl(getActivity()));
        mMessageReceiver = new DiscoveryBroadcastReceiver(mPresenter);

        root.findViewById(R.id.btnWifiSettings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.onClickWifiSettings();
            }
        });

        // restore saved device list
        if(savedInstanceState != null){
            String errorMessage = savedInstanceState.getString(STATE_ERROR_MESSAGE);
            tvErrorMessage.setText(errorMessage);
            boolean isVisible = savedInstanceState.getBoolean(STATE_IS_ERROR_VISIBLE, false);
            if(isVisible)
                errorView.setVisibility(View.VISIBLE);
            ArrayList<ParcelableDevice> deviceList = savedInstanceState.getParcelableArrayList(STATE_DEVICE_LIST);
            for(Device device : deviceList){
                mAdapter.add(device);
            }
        }
        else {
            mPresenter.onStart();
        }

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_client, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                if(mPresenter != null)
                    mPresenter.onRefreshClicked();
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
                    + " must implement OnStatusUpdateListener");
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
        if(mAdapter != null) {
            mAdapter.add(device);
        }
    }

    @Override
    public void showError(String message) {
        rvDevice.setVisibility(View.INVISIBLE);
        tvErrorMessage.setText(message);
        errorView.setVisibility(View.VISIBLE);
        mListener.onStatusUpdate(OnStatusUpdateListener.STATUS.ERROR);
    }

    @Override
    public void hideError() {
        rvDevice.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void emptyDeviceList() {
        mAdapter.setEmpty();
    }

    public void registerDiscoveryReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_SERVER_FOUND);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_CLIENT_STARTED);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_CLIENT_ENDED);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_CLIENT_ERROR);
        filter.addAction(DiscoveryService.ACTION_DISCOVERY_SERVER_NOT_FOUND);
        filter.addAction(DiscoveryService.ACTION_WIFI_OFF);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(mMessageReceiver,
                filter);
    }

    public void unRegisterDiscoveryReceiver() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()).getApplicationContext()).unregisterReceiver(mMessageReceiver);
    }

    @Override
    public void onResume() {
        registerDiscoveryReceiver();
        super.onResume();
    }

    @Override
    public void onPause() {
        unRegisterDiscoveryReceiver();
        if(mPresenter != null)
            mPresenter.onEnd();
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<ParcelableDevice> deviceList = new ArrayList<>();
        for(Device device : mAdapter.getItems()){
            deviceList.add(new ParcelableDevice(device));
        }
        outState.putParcelableArrayList(STATE_DEVICE_LIST, deviceList);
        outState.putBoolean(STATE_IS_ERROR_VISIBLE, errorView.getVisibility() == View.VISIBLE);
        outState.putString(STATE_ERROR_MESSAGE,tvErrorMessage.getText().toString());
    }

    @Override
    public void onItemClick(Device device) {
        mPresenter.onItemClicked(device);
    }
    public interface OnStatusUpdateListener{
        public enum STATUS{ READY, BUSY, ERROR }

        public void onStatusUpdate(STATUS status);
    }
}
