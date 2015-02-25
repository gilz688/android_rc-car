package ph.edu.msuiit.rccarserver.commands;

import android.util.Log;

import java.io.IOException;

import tw.com.prolific.driver.pl2303.PL2303Driver;

public class RCCar{
    private static final String TAG = "RCCar";
    public static final String ACTION_USB_PERMISSION = "ph.edu.msuiit.rccarserver.USB_PERMISSION";
    private PL2303Driver mSerial;

    public RCCar(PL2303Driver serial){
        mSerial = serial;
    }

    public void connect() throws IOException {
        Log.d(TAG, "Enter  openUsbSerial");
        if(null==mSerial)
            return;
        if(!mSerial.isConnected()) {
            int i=0;
            while(i++ < 5 && mSerial.enumerate()){
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!mSerial.InitByBaudRate(PL2303Driver.BaudRate.B9600,700)) {

            if(!mSerial.PL2303Device_IsHasPermission()) {
                throw new PermissionException();
            }

            if(mSerial.PL2303Device_IsHasPermission() && (!mSerial.PL2303Device_IsSupportChip())) {
                throw new UnsupportedChipException();
            }
        }
    }

    public void disconnect(){
        if(mSerial!=null) {
            mSerial.end();
            mSerial = null;
        }
    }

    public void send(String command) throws IOException {
        int res = mSerial.write(command.concat("\n").getBytes(), command.length() + 1);
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
