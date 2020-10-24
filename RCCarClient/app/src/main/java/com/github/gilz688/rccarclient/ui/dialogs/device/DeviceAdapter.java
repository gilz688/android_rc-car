package com.github.gilz688.rccarclient.ui.dialogs.device;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.gilz688.rccarclient.R;
import com.github.gilz688.rccarclient.databinding.ItemDeviceBinding;
import com.github.gilz688.rccarclient.model.Device;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> deviceList;
    private OnItemClickListener listener;

    public DeviceAdapter() {
        deviceList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder deviceViewHolder, int position) {
        ItemDeviceBinding binding = deviceViewHolder.getBinding();
        Device device = deviceList.get(position);
        binding.tvDeviceName.setText(device.getName());

        InetAddress address = device.getIpAddress();
        if (address != null)
            binding.tvIpAddress.setText(device.getIpAddress().getHostAddress() + ":" + device.getPort());

        binding.ivDeviceIcon.setImageResource(R.drawable.ic_rc_car);
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemDeviceBinding binding = ItemDeviceBinding.inflate(inflater, parent, false);
        return new DeviceViewHolder(binding);
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public boolean add(Device device) {
        boolean result = false;
        if (!deviceList.contains(device)) {
            result = deviceList.add(device);
            notifyDataSetChanged();
        }
        return result;
    }

    public void setEmpty() {
        deviceList = new ArrayList<>();
    }

    public List<Device> getItems() {
        return deviceList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Device device);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemDeviceBinding binding;

        public DeviceViewHolder(ItemDeviceBinding itemDeviceBinding) {
            super(itemDeviceBinding.getRoot());
            binding = itemDeviceBinding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (listener != null) {
                Device device = deviceList.get(position);
                listener.onItemClick(device);
            }
        }

        public ItemDeviceBinding getBinding() {
            return binding;
        }
    }
}
