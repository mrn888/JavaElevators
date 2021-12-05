package com.nulp.logic.strategy;

import com.nulp.logic.entities.IElevator;
import com.nulp.logic.utils.MyLogger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Logger;

public class PlainStrategy implements IElevatorStrategy {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    @Override
    public boolean shouldStopOnFloor(IElevator elevator) {
        var currentRouteList = new ArrayList<>(elevator.getCurrentRoute());
        LOGGER.info(currentRouteList + " , current floor: " + elevator.getCurrentFloor());
        return currentRouteList.get(0) == elevator.getCurrentFloor();
    }

    @Override
    public String getStrategyName() {
        return "Plain";
    }
}