package com.nulp.logic.state;

import com.nulp.logic.entities.Elevator;
import com.nulp.logic.entities.IFloor;
import com.nulp.logic.utils.MyLogger;

import java.util.logging.Logger;

public class StoppedState extends ElevatorState {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    public StoppedState(Elevator elevator) {
        super(elevator);
    }

    @Override
    public void onFloor(IFloor floor) {
        LOGGER.info("CURRENT ROUTE: " + elevator.getCurrentRoute());
    }

    @Override
    public void changeFloor() {

    }

    @Override
    public void onCall() {
        elevator.changeState(new MovingState(elevator));
    }


    @Override
    public String getState() {
        return "Stopped";
    }
}
