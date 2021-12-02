package com.nulp.logic.entities;


import java.util.ArrayList;
import java.util.LinkedHashSet;

public interface IElevator {
    public void call(int floor);
    public boolean addPassenger(Passenger passenger);
    public ArrayList<Passenger> getPassengers();
    public ArrayList<Integer> getCallingQueue();
    public LinkedHashSet<Integer> getCurrentRoute();
    public int getCurrentFloor();
}
