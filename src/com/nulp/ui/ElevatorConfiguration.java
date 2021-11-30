package com.nulp.ui;

enum ElevatorStrategy {
    Plain,
    Preemptive
}

public class ElevatorConfiguration {
    private ElevatorStrategy elevatorStrategy;
    private Integer maxWeight;
    private Integer maxPassengersNumber;

    public ElevatorStrategy getElevatorStrategy() {
        return elevatorStrategy;
    }

    public Integer getMaxPassengersNumber() {
        return maxPassengersNumber;
    }

    public Integer getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(Integer maxWeight) {
        this.maxWeight = maxWeight;
    }

    public void setElevatorStrategy(ElevatorStrategy elevatorStrategy) {
        this.elevatorStrategy = elevatorStrategy;
    }

    public void setMaxPassengersNumber(Integer maxPassengersNumber) {
        this.maxPassengersNumber = maxPassengersNumber;
    }
}

