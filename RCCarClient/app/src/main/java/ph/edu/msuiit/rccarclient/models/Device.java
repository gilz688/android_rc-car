package ph.edu.msuiit.rccarclient.models;

import java.net.InetAddress;

public class Device {
    private String name;
    private InetAddress ipAddress;
    private int port;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Device)) return false;

        Device device = (Device) o;

        if (port != device.port) return false;
        if (!ipAddress.equals(device.ipAddress)) return false;
        if (!name.equals(device.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + ipAddress.hashCode();
        result = 31 * result + port;
        return result;
    }

    public Device(String name, InetAddress ipAddress, int port) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
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

    public int getPort() {
        return port;
    }

    public void setIpAddress(InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
