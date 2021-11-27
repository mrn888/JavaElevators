package com.nulp.logic.entities;


import com.nulp.logic.configuration.BuildingConfiguration;

import java.util.ArrayList;
import java.util.TimerTask;

public class Building {
    private ArrayList<Floor> floors;
    private ArrayList<Elevator> elevators;


    private BuildingConfiguration buildingConfiguration;

    public Building(BuildingConfiguration buildingConfiguration) {
        this.buildingConfiguration = buildingConfiguration;
        createElevators();
        createFloors();
        for (var floor: floors) {
            floor.startPassengerGenerating();
        }
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
                });
            };
        };
        return passengerGenerator;
    }

    private TimerTask createElevatorOnFloorCallback(Elevator elevator) {
        var onFloorCallback = new TimerTask() {
            @Override
            public void run() {
                var floorIndex = elevator.changeFloor();
                var currentFloor= floors.get(floorIndex);
//                System.out.println("Passengers on floor[" + floorIndex + "] = " + currentFloor.passengers.size());
                elevator.removePassengers();
                elevator.getState().onFloor(currentFloor);
            }
        };
        return onFloorCallback;
    }

}