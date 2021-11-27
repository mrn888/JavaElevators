package com.nulp.logic.entities;

import com.nulp.logic.configuration.BuildingConfiguration;

import java.util.concurrent.ThreadLocalRandom;

public class Passenger {
    private int weight;
    private int floorTarget;

    Passenger(int weight) {
        this.weight = weight;
        this.floorTarget = getRandomTargetFloor(BuildingConfiguration.getInstance().getFloors());
    }

    public int getWeight() {
        return weight;
    }

    public int getFloorTarget() {
        return floorTarget;
    }

    public static int getRandomPassengerWeight() {
        return ThreadLocalRandom.current().nextInt(20, 150);
    }

    public int getRandomTargetFloor(int maxFloor) {
        return ThreadLocalRandom.current().nextInt(1, maxFloor);
    }
}
