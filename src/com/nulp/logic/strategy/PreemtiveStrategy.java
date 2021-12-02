package com.nulp.logic.strategy;


import com.nulp.logic.entities.IElevator;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class PreemtiveStrategy implements IElevatorStrategy {

    @Override
    public boolean shouldStopOnFloor(IElevator elevator) {
        var currentRouteList = new ArrayList<>(elevator.getCurrentRoute());
        System.out.println(currentRouteList + "Current floor: " + elevator.getCurrentFloor());
        return elevator.getCurrentRoute().contains(elevator.getCurrentFloor());
    }


    @Override
    public String getStrategyName() {
        return "Preemtive";
    }
}
