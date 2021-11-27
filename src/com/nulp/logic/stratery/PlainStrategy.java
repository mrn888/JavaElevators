package com.nulp.logic.stratery;

import com.nulp.logic.entities.IElevator;

import java.util.LinkedHashSet;

public class PlainStrategy implements IElevatorStrategy {
    @Override
    public LinkedHashSet<Integer> buildRoute(IElevator elevator) {
        var route = new LinkedHashSet<Integer>();

        for (var passenger: elevator.getPassengers()) {
            route.add(passenger.getFloorTarget());
        }
        for (var floor: elevator.getCallingQueue()) {
            if(!route.contains(floor))
                route.add(floor);
        }
        System.out.println(route);
        return route;
    }
}