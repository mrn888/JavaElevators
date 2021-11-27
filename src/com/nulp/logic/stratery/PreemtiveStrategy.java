package com.nulp.logic.stratery;


import com.nulp.logic.entities.IElevator;

import java.util.LinkedHashSet;

public class PreemtiveStrategy implements IElevatorStrategy {

    @Override
    public LinkedHashSet<Integer> buildRoute(IElevator elevator) {
        var route = new LinkedHashSet<Integer>();
        return route;
    }
}
