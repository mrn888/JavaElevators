package com.nulp.logic.configuration;

import com.nulp.logic.stratery.IElevatorStrategy;

public class ElevatorConfiguration {
    public IElevatorStrategy strategy;
    public int id;
    public int speed;
    public int capacity;
    public int weight;

    public ElevatorConfiguration(IElevatorStrategy strategy, int id, int speed, int capacity, int weight) {
        this.id = id;
        this.speed = speed;
        this.capacity = capacity;
        this.weight = weight;
        this.strategy = strategy;
    };
}