package com.github.gilz688.rccarclient.ui.dialogs.device;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.databinding.FragmentDeviceBinding;
import com.github.gilz688.rccarclient.discovery.DiscoveryBroadcastReceiver;
import com.github.gilz688.rccarclient.discovery.DiscoveryService;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.util.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Objects;

public class DeviceDialog extends DialogFragment implements DeviceAdapter.OnItemClickListener {

    public static final String TAG = "SelectDevice";

    private FragmentDeviceBinding binding;
    private DeviceViewModel viewModel;

    // handler for received Intents for the "server_discovery" event
    BroadcastReceiver mMessageReceiver;

    DeviceDialogListener mListener;

    public DeviceDialog(DeviceDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        );

        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        binding = FragmentDeviceBinding.inflate(layoutInflater);
        builder.setView(binding.getRoot());
        builder.setTitle("Connect to Device");
        builder.setIcon(R.drawable.ic_connect);

        // Allow dialog to be dismissed by clicking outside the borders
        setCancelable(true);

        // Start device discovery
        startDiscovery();

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DeviceViewModel.class);
        mMessageReceiver = new DiscoveryBroadcastReceiver(viewModel);

        // Configure recycler view
        final DeviceAdapter mAdapter = new DeviceAdapter();
        binding.rvDevice.setAdapter(mAdapter);

        // Tweak to speed up a fixed size recycler view
        binding.rvDevice.setHasFixedSize(true);

        // Listen to clicks
        mAdapter.setOnItemClickListener(this);

        LifecycleOwner lifecycleOwner = getViewLifecycleOwner();

        // Observe when there are changes to view state
        viewModel.getStateData().observe(lifecycleOwner, this::handleStateChange);

        // Observe when device is added to list
        viewModel.getDiscoveredDeviceData().observe(lifecycleOwner, (event) -> {
            Device device = event.getContentIfNotHandledOrReturnNull();
            if (device != null) {
                mAdapter.add(device);
            }
        });

        // Observe when error is received
        viewModel.getErrorData().observe(lifecycleOwner, (event) -> {
            String error = event.getContentIfNotHandledOrReturnNull();
            binding.tvErrorMessage.setText(error);
        });

        return binding.getRoot();
    }

    private void handleStateChange(Event<DeviceViewModel.DeviceViewState> event) {
        DeviceViewModel.DeviceViewState state = event.getContentIfNotHandledOrReturnNull();
        if (state == null) return;

        boolean showLoader = false;
        boolean showError = false;
        boolean showDeviceList = false;
        View inView = binding.rvDevice;
        View outView = binding.animationView;
        Log.d("DeviceDialog", "state: " + state);
        switch (state) {
            case DISCOVERED:
                showDeviceList = true;
                break;
            case DISCOVERING:
                showLoader = true;
                break;
            case ERROR:
                showError = true;
                break;
            default:
        }
        binding.animationView.setVisibility(showLoader ? View.VISIBLE : View.INVISIBLE);
        binding.llError.setVisibility(showError ? View.VISIBLE : View.INVISIBLE);
        binding.rvDevice.setVisibility(showDeviceList ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void onItemClick(Device device) {
        mListener.onDeviceSelected(device);

        dismiss();
    }

    public void registerDiscoveryReceiver() {
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

    public void startDiscovery() {
        Activity activity = requireActivity();
        Intent intent = new Intent(activity, DiscoveryService.class);
        intent.setAction(DiscoveryService.INTENT_START_DISCOVERY);
        activity.startService(intent);
    }

    @Override
    public void onResume() {
        registerDiscoveryReceiver();
        super.onResume();
    }

    @Override
    public void onPause() {
        unRegisterDiscoveryReceiver();
        super.onPause();
    }

    public interface DeviceDialogListener {
        void onDeviceSelected(Device device);
    }
}