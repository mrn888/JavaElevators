package com.nulp.logic.stratery;


import com.nulp.logic.entities.IElevator;

import java.util.LinkedHashSet;

public interface IElevatorStrategy {
    public LinkedHashSet<Integer> buildRoute(IElevator floor);
}
