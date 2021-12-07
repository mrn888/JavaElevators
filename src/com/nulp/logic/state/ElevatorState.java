package com.nulp.logic.state;

import com.nulp.logic.entities.Elevator;
import com.nulp.logic.entities.IFloor;

public abstract class ElevatorState {
    protected Elevator elevator;

    ElevatorState(Elevator elevator) {
        this.elevator = elevator;
    }
    public abstract void onFloor(IFloor floor);
    public abstract void changeFloor();
    public abstract void onCall();
    public abstract String getState();

    public void checkOnFloor(int floor) {
        elevator.changeFloor();
    }
}