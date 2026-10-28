package com.github.gilz688.rccarclient.view;

import android.app.Dialog;
import android.content.BroadcastReceiver;
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

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.RCCarClientApplication;
import com.github.gilz688.rccarclient.databinding.FragmentDeviceBinding;
import com.github.gilz688.rccarclient.inject.RCCarComponent;
import com.github.gilz688.rccarclient.model.Device;
import com.github.gilz688.rccarclient.adapter.DeviceAdapter;
import com.github.gilz688.rccarclient.viewmodel.DeviceDialogViewModel;
import com.github.gilz688.rccarclient.util.Event;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DeviceDialogFragment extends DialogFragment implements DeviceAdapter.OnItemClickListener {

    public static final String TAG = "SelectDevice";

    private FragmentDeviceBinding binding;
    private DeviceDialogViewModel viewModel;

    // handler for received Intents for the "server_discovery" event
    BroadcastReceiver mMessageReceiver;

    DeviceDialogListener mListener;

    public DeviceDialogFragment(DeviceDialogListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(
                requireContext(),
                R.style.MyRounded_MaterialComponents_MaterialAlertDialog
        );

        // Inject the View Model
        viewModel = new ViewModelProvider(this).get(DeviceDialogViewModel.class);
        RCCarComponent component = ((RCCarClientApplication) requireActivity().getApplication()).getComponent();
        component.inject(viewModel);

        // Initialize View Binding
        LayoutInflater layoutInflater = requireActivity().getLayoutInflater();
        binding = FragmentDeviceBinding.inflate(layoutInflater);
        builder.setView(binding.getRoot());
        builder.setTitle("Connect to Device");
        builder.setIcon(R.drawable.ic_connect);

        // Allow dialog to be dismissed by clicking outside the borders
        setCancelable(true);

        // Start device discovery
        viewModel.startDiscovery();

        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewModel = new ViewModelProvider(this).get(DeviceDialogViewModel.class);

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

        return binding.getRoot();
    }

    private void handleStateChange(Event<DeviceDialogViewModel.DeviceViewState> event) {
        DeviceDialogViewModel.DeviceViewState state = event.getContentIfNotHandledOrReturnNull();
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

    public interface DeviceDialogListener {
        void onDeviceSelected(Device device);
    }
}