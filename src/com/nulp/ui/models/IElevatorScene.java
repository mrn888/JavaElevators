package com.nulp.ui.models;

import com.nulp.logic.entities.Elevator;

public interface IElevatorScene {
    public void updatePassengers();
    public void moveElevator(Elevator elevator, int floor);
}
