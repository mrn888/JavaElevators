package com.nulp.logic.strategy;


import com.nulp.logic.entities.IElevator;


public interface IElevatorStrategy {
    public boolean shouldStopOnFloor(IElevator elevator);
    public String getStrategyName();
}
