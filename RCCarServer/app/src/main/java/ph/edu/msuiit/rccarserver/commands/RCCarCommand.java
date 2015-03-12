package ph.edu.msuiit.rccarserver.commands;

import com.google.gson.annotations.Expose;

import java.io.IOException;

public class RCCarCommand implements Command{
    private String command;

    @Expose(serialize = false)
    private RCCar car;

    public RCCarCommand(RCCar car, String command){
        this.command = command;
        this.car = car;
    }

    @Override
    public void execute() {
        try {
            car.send(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getCommand() {
        return command;
    }
}
