package ph.edu.msuiit.rccarclient.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import ph.edu.msuiit.rccarclient.R;
import ph.edu.msuiit.rccarclient.models.Device;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>{
    private List<Device> deviceList;
    private OnItemClickListener listener;

    public DeviceAdapter(){
        deviceList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    @Override
    public void onBindViewHolder(DeviceViewHolder deviceViewHolder, int position) {
        Device device = deviceList.get(position);
        deviceViewHolder.tvDeviceName.setText(device.getName());

        InetAddress address = device.getIpAddress();
        if(address != null)
            deviceViewHolder.tvIpAddress.setText(device.getIpAddress().getHostAddress());

        deviceViewHolder.ivDeviceIcon.setImageResource(R.mipmap.ic_launcher);
    }

    @Override
    public DeviceViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View deviceView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_device, parent, false);
        return new DeviceViewHolder(deviceView);
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public boolean add(Device device) {
        boolean result = false;
        if (!deviceList.contains(device)){
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

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    public static interface OnItemClickListener{
        public void onItemClick(Device device);
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView tvDeviceName;
        protected TextView tvIpAddress;
        protected ImageView ivDeviceIcon;

        public DeviceViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            tvDeviceName = (TextView) itemView.findViewById(R.id.tvDeviceName);
            tvIpAddress = (TextView) itemView.findViewById(R.id.tvIpAddress);
            ivDeviceIcon = (ImageView) itemView.findViewById(R.id.ivDeviceIcon);
        }

        @Override
        public void onClick(View v) {
            int position = this.getPosition();
            if(listener != null){
                Device device = deviceList.get(position);
                listener.onItemClick(device);
            }
        }
    }
}
