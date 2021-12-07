package com.nulp.logic.entities;


import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;

public interface IElevator {
    public void call(int floor);
    public boolean addPassenger(Passenger passenger);
    public ArrayList<Passenger> getPassengers();
    public LinkedList<Integer> getCurrentRoute();
    public int getCurrentFloor();
}
