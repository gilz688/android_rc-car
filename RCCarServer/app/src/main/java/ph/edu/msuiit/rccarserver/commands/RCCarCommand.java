package ph.edu.msuiit.rccarserver.commands;

import java.io.IOException;

public class RCCarCommand implements Command{
    private String command;
    RCCar car;

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
}
