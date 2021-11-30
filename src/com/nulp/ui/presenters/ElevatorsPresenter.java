package com.nulp.ui.presenters;

import com.nulp.ui.models.Elevator;
import com.nulp.ui.models.IElevatorsScene;

import com.nulp.ui.models.Passenger;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

// our custom logic
public class ElevatorsPresenter  {

    private final float elevatorWeight;
    private final int elevatorSize;

    private final int floorsCounts;
    private final int elevatorsCount;

    private final int PERSON_SPAWN_RATE = 2000;

    private IElevatorsScene view;

    private final List<Elevator> elevators;
    private final List<Passenger> passengers = new ArrayList<>();

    //private final PersonSpawner personSpawner;

    public ElevatorsPresenter(ElevatorsSceneArgs args, List<Elevator> elevators) {
        this.elevators = elevators;
        this.elevatorWeight = 600f;
        this.elevatorSize = args.passengersCount;
        this.floorsCounts = args.floorsCount;
        this.elevatorsCount = args.elevatorsCount;

//        List<BuildingFloor> floors = new FloorsCreator(elevatorsCount).create(floorsCounts);
//
//        List<data.elevator.Elevator> elevators = new ElevatorsCreator(
//                elevatorWeight,
//                elevatorSize,
//                resolveStrategy(args.strategyNumber)
//        )
//                .withListener(this)
//                .create(elevatorsCount);
//
//        Building building = new BuildingImpl(floors, elevators);
//
//        this.elevators = Collections.unmodifiableList(parseElevators(elevators));
//
//        personSpawner = new PersonSpawner(
//                PERSON_SPAWN_RATE,
//                person -> startPersonThread(building, person)
//        );
//        personSpawner.startSpawn();
    }

    public void setView(IElevatorsScene view) {
        this.view = view;
    }

    public void onElevatorFloorChanged(int elevatorID, int newFloor) {
        Elevator elevator = findElevator(elevatorID);
        if (elevator != null) {
            elevator.setFloor(newFloor + 1);
        }
    }

    public void onElevatorDeparted(int elevatorID) {
        Elevator elevator = findElevator(elevatorID);
//        if (elevator != null) {
//            elevator.setState(ElevatorState.MOVING);
//        }
    }

    public void onElevatorArrived(int elevatorID) {
        Elevator elevator = findElevator(elevatorID);
//        if (elevator != null) {
//            elevator.getElevator().setIsMoving(false);
//            elevator.setState(ElevatorState.WAITING);
//        }

        // TODO notify elevator

        // to continue animations
        // call move elevator
        // or
        // call move passenger into elevator
        // call move passenger from elevator
    }

    public void onPassengerSpawned(int passengerID) {

    }

    public void onPassengerEnteredElevator(int passengerID, int elevatorID) {
        // TODO find elevator and reset delayed move timer
    }

    public void onPassengerExitedElevator(int passengerID, int elevatorID) {
        // TODO find elevator and reset delayed move timer
    }

    public void onPassengerDeleted(int passengerID) {
    }

    // ElevatorScene needs
    public int getFloorsCount() {
        return floorsCounts;
    }

    public int getElevatorsCount() {
        return elevatorsCount;
    }

    public Passenger findPassenger(int passengerID) {
        for (Passenger passenger : passengers) {
            if (passenger.getId() == passengerID)
                return passenger;
        }

        return null;
    }

    public Elevator findElevator(int elevatorID) {
        for (Elevator elevator : elevators) {
            if (elevator.getId() == elevatorID)
                return elevator;
        }

        return null;
    }

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public List<Elevator> getElevators() {
        return elevators;
    }


    // test methods
    public void generateElevatorMovementCall() {
        if (view == null || elevators.size() == 0)
            return;
        Random random = new Random();
        int index = random.nextInt(getElevatorsCount());
        int newFloor = random.nextInt(getFloorsCount()) + 1;

        view.moveElevatorToFloor(elevators.get(index).getId(), newFloor);
    }

    public void generateElevatorMovementCall(int elevatorID) {
        if (view == null || elevators.size() == 0)
            return;
        Random random = new Random();
        int newFloor = random.nextInt(getFloorsCount()) + 1;
        view.moveElevatorToFloor(elevatorID, newFloor);
    }
    // test methods
    // ElevatorScene needs


}
