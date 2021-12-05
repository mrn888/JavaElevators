package com.nulp.logic.configuration;

import com.nulp.logic.strategy.IElevatorStrategy;
import com.nulp.logic.utils.MyLogger;

import java.util.logging.Logger;

public class ElevatorConfiguration {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    public IElevatorStrategy strategy;
    public int id;
    public int speed;
    public int capacity;
    public int weight;

    public ElevatorConfiguration() {}
    public ElevatorConfiguration(IElevatorStrategy strategy, int id, int speed, int capacity, int weight) {
        this.id = id;
        this.speed = speed;
        this.capacity = capacity;
        this.weight = weight;
        this.strategy = strategy;
        LOGGER.info("Created elevator configuration");
    }

    @Override
    public String toString() {
        return "ElevatorConfiguration{" +
                "strategy=" + strategy +
                ", id=" + id +
                ", speed=" + speed +
                ", capacity=" + capacity +
                ", weight=" + weight +
                '}';
    }
}