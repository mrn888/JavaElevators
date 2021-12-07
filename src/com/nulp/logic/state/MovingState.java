package com.nulp.logic.state;


import com.nulp.logic.entities.Elevator;
import com.nulp.logic.entities.IFloor;
import com.nulp.logic.entities.Passenger;
import com.nulp.logic.utils.MyLogger;

import java.util.Iterator;
import java.util.logging.Logger;

public class MovingState extends ElevatorState {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    MovingState(Elevator elevator) {
        super(elevator);
    }

    @Override
    public void onFloor(IFloor floor) {
        if(!elevator.getStrategy().shouldStopOnFloor(elevator)) return;
        elevator.removePassengers();
        boolean isEmpty = elevator.popFromRoute();
        LOGGER.info("CURRENT ROUTE: " + elevator.getCurrentRoute());

        Iterator<Passenger> i = floor.getPassengers(elevator.getId()).iterator();
        while (i.hasNext()) {
            var passenger = i.next();
            boolean isAdded = elevator.addPassenger(passenger);
            if(isAdded)
                i.remove();
        }
        if(elevator.getCurrentRoute().size() == 0) {
            elevator.changeState(new StoppedState(elevator));
        }
    }

    @Override
    public void changeFloor() {
        elevator.changeFloor();
    }

    @Override
    public void onCall() {
    }


    @Override
    public String getState() {
        return "Moving";
    }
}
