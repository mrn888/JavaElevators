package com.nulp.logic.state;


import com.nulp.logic.entities.Elevator;
import com.nulp.logic.entities.IFloor;
import com.nulp.logic.entities.Passenger;

import java.util.Iterator;

public class MovingState extends ElevatorState {
    MovingState(Elevator elevator) {
        super(elevator);
    }

    @Override
    public void onFloor(IFloor floor) {
        if(!elevator.getStrategy().shouldStopOnFloor(elevator)) return;
        boolean isEmpty = elevator.popFromRoute();
        if(isEmpty) {
            elevator.changeState(new StoppedState(elevator));
        }
        elevator.buildRoute();
        elevator.defineDirection();
        Iterator<Passenger> i = floor.getPassengers(elevator.getId()).iterator();
        while (i.hasNext()) {
            var passenger = i.next();
            boolean isAdded = elevator.addPassenger(passenger);
            if(isAdded)
                i.remove();
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
    public void onStatus() {

    }

    @Override
    public String getState() {
        return "Moving";
    }
}
