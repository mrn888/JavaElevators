package com.nulp.logic.configuration;


import com.nulp.logic.utils.MyLogger;

import java.util.ArrayList;
import java.util.logging.Logger;

public class BuildingConfiguration {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    private static BuildingConfiguration instance;
    private ArrayList<ElevatorConfiguration> elevatorConfiguration;
    private int passengerFrequency;
    private int floors;

    public static void setInstance(ArrayList<ElevatorConfiguration> elevatorConfiguration, int floors, int passengerFrequency) {
        instance = new BuildingConfiguration(elevatorConfiguration, floors, passengerFrequency);
    }

    public static BuildingConfiguration getInstance() {
        return instance;
    }

    private BuildingConfiguration(ArrayList<ElevatorConfiguration> elevatorConfiguration, int floors, int passengerFrequency) {
        this.elevatorConfiguration = elevatorConfiguration;
        this.passengerFrequency = passengerFrequency;
        this.floors = floors;
        LOGGER.info("Created building configuration");
    }


    public int getFloors() {
        return floors;
    }

    public ArrayList<ElevatorConfiguration> getElevatorConfiguration() {
        return elevatorConfiguration;
    }

    public int getPassengerFrequency() {
        return passengerFrequency;
    }
}
