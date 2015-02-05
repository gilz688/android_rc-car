package ph.edu.msuiit.rccarclient.discovery;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.net.UnknownHostException;

import ph.edu.msuiit.rccarclient.models.Device;

public class ParcelableDevice extends Device implements Parcelable{
    public static final Creator<ParcelableDevice> CREATOR = new Creator<ParcelableDevice>() {
        public ParcelableDevice createFromParcel(Parcel in) {
            return new ParcelableDevice(in);
        }

        public ParcelableDevice[] newArray(int size) {
            return new ParcelableDevice[size];
        }
    };

    public ParcelableDevice(String name, InetAddress address) {
        super(name, address);
    }

    public ParcelableDevice(Device device) {
        super(device.getName(),device.getIpAddress());
    }

    private ParcelableDevice(Parcel in) {
        super(null,null);
        String name = in.readString();
        setName(name);
        byte[] rawAddress = new byte[4];
        in.readByteArray(rawAddress);
        InetAddress address = null;
        try {
            address = InetAddress.getByAddress(rawAddress);
            setIpAddress(address);
        } catch (UnknownHostException e) {

        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeByteArray(getIpAddress().getAddress());
    }
}
