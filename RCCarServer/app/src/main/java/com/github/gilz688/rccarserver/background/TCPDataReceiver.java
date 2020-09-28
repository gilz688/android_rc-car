package com.github.gilz688.rccarserver.background;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;

import com.google.gson.JsonSyntaxException;

import java.io.IOException;

import com.github.gilz688.rccarserver.R;
import com.github.gilz688.rccarserver.common.RCCommand;

public class TCPDataReceiver implements TCPServer.TCPServerListener{
    private static final String TAG = "TCPDataReceiver";
    private final MediaPlayer mp;
    private RCCar mCar;

    public TCPDataReceiver(Context context, RCCar car){
        mCar = car;
        mp = MediaPlayer.create(context, R.raw.car_horn);
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
                case "horn":
                    mp.start();
                    break;
                case "stop horn":

                    break;
                default:
            }
        } catch (JsonSyntaxException e){
            e.printStackTrace();
        }
    }
}
