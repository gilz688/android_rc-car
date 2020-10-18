package com.github.gilz688.rccarserver.background;

import android.hardware.usb.UsbDeviceConnection;
import android.util.Log;

import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.io.IOException;

public class RCCar{
    private static final String TAG = "RCCar";
    public static final String ACTION_USB_PERMISSION = "com.github.gilz688.rccarserver.USB_PERMISSION";
    private final UsbDeviceConnection mConnection;
    private UsbSerialPort mPort;

    public RCCar(UsbSerialPort port, UsbDeviceConnection connection){
        mPort = port;
        mConnection = connection;
    }

    public void connect() throws IOException {
        if(mPort == null) return;

        mPort.setParameters(9600, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE);
        mPort.open(mConnection);
    }

    public void disconnect() {
        if(mPort == null) return;

        try {
            mPort.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPort = null;
    }

    public void send(String command) throws IOException {
        Log.d("RCCar",command);
        int res = mPort.write(command.concat("\r").getBytes(), command.length() + 1);
        if( res < 0 ) {
            throw new SerialSendingException();
        }
    }

    private class PermissionException extends IOException {
        public PermissionException(){
            super("Cannot open, maybe no permission.");
        }
    }

    private class UnsupportedChipException extends IOException {
        public UnsupportedChipException(){
            super("Cannot open, maybe this chip has no support, please use PL2303HXD / RA / EA chip.");
        }
    }

    private class SetupFailedException extends IOException {
        public SetupFailedException(){
            super("Cannot setup, please check serial device settings.");
        }
    }

    private class SerialSendingException extends IOException {
        public SerialSendingException(){
            super("Cannot write to serial, please check serial device settings.");
        }
    }
}
