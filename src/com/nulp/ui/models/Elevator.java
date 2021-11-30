package com.nulp.ui.models;

import com.nulp.logic.state.ElevatorState;
import com.nulp.logic.state.StoppedState;

public class Elevator {
    private int id;
    private int floor;
    private ElevatorState state;

    private com.nulp.logic.entities.Elevator elevator;

    public Elevator(int id, int floor, com.nulp.logic.entities.Elevator elevator) {
        this.id = id;
        this.floor = floor;
        this.state = new StoppedState(elevator);
        this.elevator = elevator;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public com.nulp.logic.entities.Elevator getElevator() {
        return elevator;
    }

    public ElevatorState getState() {
        return state;
    }

    public void setState(ElevatorState state) {
        this.state = state;
    }
}
