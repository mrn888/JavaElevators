package com.nulp.logic.entities;


import com.nulp.logic.configuration.BuildingConfiguration;
import com.nulp.ui.models.IElevatorScene;

import java.util.ArrayList;
import java.util.TimerTask;

public class Building {
    private ArrayList<Floor> floors;
    private ArrayList<Elevator> elevators;

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
        for (var floor: floors) {
            floor.startPassengerGenerating();
        }
    }

    public void startElevatorsMovement() {
        for (var elevator: elevators) {
            elevator.startElevatorMovement();
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
        for(int i = 0; i < elevatorsAmount; ++i) {
            var elevator = new Elevator(i,
                    buildingConfiguration.getElevatorConfiguration().get(i));
            elevator.setOnFloorCallback(createElevatorOnFloorCallback(elevator));
            elevators.add(elevator);
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