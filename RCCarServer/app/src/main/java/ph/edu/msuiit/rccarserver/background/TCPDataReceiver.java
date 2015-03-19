package ph.edu.msuiit.rccarserver.background;

import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import ph.edu.msuiit.rccarserver.common.RCCommand;

public class TCPDataReceiver implements TCPServer.TCPServerListener{
    private static final String TAG = "TCPDataReceiver";
    private RCCar mCar;

    public TCPDataReceiver(RCCar car){
        mCar = car;
    }

    @Override
    public void onDataReceive(String line){
        Log.d(TAG,"received: " + line);

        try {
            RCCommand command = RCCommand.newInstanceFromJson(line.trim());
            if(command == null)
                return;

            String cmd = command.getCommand();
            if (cmd == null)
                return;

            Log.d(TAG,"cmd: " + cmd);
            switch (cmd.toLowerCase()){
                case "move":
                case "steer":
                    Double param = (Double) command.getData("param");
                    int value = param.intValue();
                    try {
                        mCar.send(cmd + " " + value);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "get_location":
                    break;
                default:
            }
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }
    }
}
