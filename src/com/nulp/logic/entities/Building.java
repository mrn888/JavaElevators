package com.nulp.logic.entities;


import com.nulp.logic.configuration.BuildingConfiguration;
import com.nulp.logic.utils.MyLogger;
import com.nulp.ui.models.IElevatorScene;

import java.util.ArrayList;
import java.util.TimerTask;
import java.util.logging.Logger;

public class Building {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    private ArrayList<Floor> floors;
    private ArrayList<Elevator> elevators;
    private ArrayList<Thread> elevatorThreads;

    private BuildingConfiguration buildingConfiguration;
    private IElevatorScene elevatorScene;

    public ArrayList<Elevator> getElevators() {
        return elevators;
    }
    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public Building(BuildingConfiguration buildingConfiguration, IElevatorScene elevatorScene) {
        this.buildingConfiguration = buildingConfiguration;
        this.elevatorScene = elevatorScene;
        createElevators();
        createFloors();
    }

    public void startPassengerGenerating() {
        LOGGER.info("Started passengers generating");
        for (var floor: floors) {
            floor.startPassengerGenerating();
        }
    }

    public void stopPassengerGenerating() {
        LOGGER.info("Stop passengers generating");
        for (var floor: floors) {
            floor.stopPassengerGenerating();
        }
    }

    public void startElevatorsMovement() {
        LOGGER.info("Started elevators movement");
        for (var elevator: elevators) {
            elevator.startElevatorMovement();
        }
    }

    public void stopElevatorsMovement() {
        LOGGER.info("Stop elevators movement");
        for (var elevator: elevators) {
            elevator.stopElevatorMovement();
        }
        for (var elevatorTh: elevatorThreads) {
            elevatorTh.interrupt();
        }
    }

    private void createFloors() {
        floors = new ArrayList<>(buildingConfiguration.getFloors());
        for(int i = 0; i < buildingConfiguration.getFloors(); ++i) {
            floors.add(new Floor(i, buildingConfiguration.getPassengerFrequency(), createPassengerGenerator()));
        }

    }
    private void createElevators() {
        int elevatorsAmount = buildingConfiguration.getElevatorConfiguration().size();
        elevators = new ArrayList<>(elevatorsAmount);
        elevatorThreads = new ArrayList<>(elevatorsAmount);
        for(int i = 0; i < elevatorsAmount; ++i) {
            var elevator = new Elevator(i,
                    buildingConfiguration.getElevatorConfiguration().get(i));
            elevator.setOnFloorCallback(createElevatorOnFloorCallback(elevator));
            elevators.add(elevator);

            Thread elevatorThread = new Thread(elevator);
            elevatorThread.start();
            elevatorThreads.add(elevatorThread);

            LOGGER.info("Created elevator with configuration " + buildingConfiguration.getElevatorConfiguration().get(i));
        }
    }

    private TimerTask createPassengerGenerator() {
        var passengerGenerator = new TimerTask() {
            @Override
            public void run() {
                floors.forEach(floor -> {
                    floor.generatePassengers();
                    elevators.forEach(elevator -> elevator.call(floor.id));
                    elevatorScene.updatePassengers();
                });
            };
        };
        return passengerGenerator;
    }

    private TimerTask createElevatorOnFloorCallback(Elevator elevator) {
        var onFloorCallback = new TimerTask() {
            @Override
            public void run() {
                elevator.getState().changeFloor();
                var floorIndex = elevator.getCurrentFloor();
                var currentFloor= floors.get(floorIndex);

                elevator.removePassengers();
                elevator.getState().onFloor(currentFloor);
                elevatorScene.moveElevator(elevator, floorIndex);
            }
        };
        return onFloorCallback;
    }

}