package ph.edu.msuiit.rccarserver;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerConfiguration {
    private static final String TAG = "ServerConfiguration";
    private WifiManager mWifiManager;

    public ServerConfiguration(WifiManager manager){
        mWifiManager = manager;
    }

    public String getWifiSSID(){
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public String getWifiIpAddress(){
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

        // IP Address in integer format
        int ipInt = wifiInfo.getIpAddress();

        // Converts the IP Address to bytes
        byte[] ipBytes = new byte[] {
                (byte)((ipInt       ) & 0xff),
                (byte)((ipInt >>>  8) & 0xff),
                (byte)((ipInt >>> 16) & 0xff),
                (byte)((ipInt >>> 24) & 0xff)
        };
        String address = null;
        try {
            address = InetAddress.getByAddress(ipBytes).getHostAddress();
        } catch(UnknownHostException e){
            Log.e(TAG, e.getMessage());
        }
        return address;
    }
}
