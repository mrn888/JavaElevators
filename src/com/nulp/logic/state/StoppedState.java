package com.nulp.logic.state;

import com.nulp.logic.entities.Elevator;
import com.nulp.logic.entities.IFloor;

public class StoppedState extends ElevatorState {

    public StoppedState(Elevator elevator) {
        super(elevator);
    }

    @Override
    public void onFloor(IFloor floor) {

    }

    @Override
    public void onCall() {
        elevator.buildRoute();
        elevator.defineDirection();
        elevator.changeState(new MovingState(elevator));
    }

    @Override
    public void onStatus() {

    }

    @Override
    public String getState() {
        return "Stopped";
    }
}
