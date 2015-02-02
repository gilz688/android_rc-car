package ph.edu.msuiit.rccarclient.models;

import java.net.InetAddress;

public class Device {
    private String name;

    private InetAddress ipAddress;

    public Device(String name, InetAddress ipAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }
}
