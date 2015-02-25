package ph.edu.msuiit.rccarclient.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ParcelableDevice extends Device implements Parcelable{
    public static final Creator<ParcelableDevice> CREATOR = new Creator<ParcelableDevice>() {
        public ParcelableDevice createFromParcel(Parcel in) {
            return new ParcelableDevice(in);
        }

        public ParcelableDevice[] newArray(int size) {
            return new ParcelableDevice[size];
        }
    };

    public ParcelableDevice(String name, InetAddress address, int port) {
        super(name, address, port);
    }

    public ParcelableDevice(Device device) {
        super(device.getName(),device.getIpAddress(), device.getPort());
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
        int port = in.readInt();
        setPort(port);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getName());
        dest.writeByteArray(getIpAddress().getAddress());
        dest.writeInt(getPort());
    }
}