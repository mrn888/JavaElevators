package com.nulp.logic.strategy;

import com.nulp.logic.entities.IElevator;

import java.util.LinkedHashSet;

public class PlainStrategy implements IElevatorStrategy {

    @Override
    public boolean shouldStopOnFloor(IElevator elevator) {
        return false;
    }

    @Override
    public String getStrategyName() {
        return "Plain";
    }
}