package com.nulp.logic.entities;


import com.nulp.logic.configuration.BuildingConfiguration;
import com.nulp.logic.configuration.ElevatorConfiguration;
import com.nulp.logic.state.ElevatorState;
import com.nulp.logic.state.StoppedState;
import com.nulp.logic.strategy.IElevatorStrategy;
import com.nulp.logic.utils.ElevatorDirection;
import com.nulp.logic.utils.MyLogger;

import java.util.*;
import java.util.logging.Logger;

public class Elevator implements IElevator {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    private int id;
    private ElevatorState state;
    private ElevatorDirection direction;
    private IElevatorStrategy strategy;
    private ElevatorConfiguration configuration;
    private ArrayList<Passenger> passengers;
    private LinkedHashSet<Integer> currentRoute;
    private ArrayList<Integer> callingQueue;
    private Timer onFloorTimer;
    private TimerTask onFloorCallback;
    private int currentFloor;

    Elevator(int id, ElevatorConfiguration configuration) {
        this.id = id;
        this.configuration = configuration;
        this.currentFloor = 0;
        this.direction = ElevatorDirection.UP;
        this.onFloorTimer = new Timer();
        this.state = new StoppedState(this);
        this.strategy = configuration.strategy;
        this.passengers = new ArrayList<>();
        this.callingQueue = new ArrayList<>();
        this.currentRoute = new LinkedHashSet<>();
        LOGGER.info("Created elevator : " + this);
    }
    public LinkedHashSet<Integer> getCurrentRoute() {
        return currentRoute;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }
    public IElevatorStrategy getStrategy() {
        return strategy;
    }
    public int getId() {
        return id;
    }

    public void setOnFloorCallback(TimerTask onFloorCallback) {
        this.onFloorCallback = onFloorCallback;
    }


    public void startElevatorMovement() {
//        System.out.println(configuration.speed);
        onFloorTimer.schedule(onFloorCallback, 0, configuration.speed);
    }

    public int changeFloor() {
        defineDirection();
        int nextFloor = currentFloor + direction.getValue();
        if(isAbleToChangeFloor(nextFloor)) {
            currentFloor = nextFloor;
        }
        LOGGER.info("Elevator " + id + " on floor " + currentFloor);

        return currentFloor;
    }

    public void removePassengers() {
        Iterator<Passenger> i = passengers.iterator();
        while (i.hasNext()) {
            var passenger = i.next();
            if(passenger.getFloorTarget() == currentFloor) {
                i.remove();
//                System.out.println("Passenger goes: " + passenger);
            }
        }
    }

    @Override
    public void call(int floor) {
        callingQueue.add(floor);
        state.onCall();
    }

    public void buildRoute() {
        var route = new LinkedHashSet<Integer>();

        for (var passenger: getPassengers()) {
            route.add(passenger.getFloorTarget());

        }
        for (var floor: getCallingQueue()) {
            if(!route.contains(floor))
                route.add(floor);
        }
        currentRoute = route;
    }

    public boolean popFromRoute() {
        currentRoute.remove(currentFloor);
        return currentRoute.isEmpty();
    }

    public void defineDirection() {
        var currentRouteList = new ArrayList<>(currentRoute);
        if(currentRouteList.get(0) > currentFloor) {
            direction = ElevatorDirection.UP;
        } else if(currentRouteList.get(0) < currentFloor){
            direction = ElevatorDirection.DOWN;
        }
//        System.out.println("Direction: " + direction);
    }

    @Override
    public boolean addPassenger(Passenger passenger) {
        if(isAbleToAddPassenger(passenger)) {
            this.passengers.add(passenger);
//            System.out.println("Added passenger: " + passenger.getFloorTarget());
            return true;
        } else {

        }
        return false;
    }

    @Override
    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }

    @Override
    public ArrayList<Integer> getCallingQueue() {
        return callingQueue;
    }

    private boolean isAbleToAddPassenger(Passenger passenger) {
        return getCurrentWeight() + passenger.getWeight() <= configuration.weight
                && passengers.size() <= configuration.capacity;
    }

    private boolean isAbleToChangeFloor(int nextFloor) {
        return nextFloor < BuildingConfiguration.getInstance().getFloors() &&
                nextFloor >= 0;
    }

    public int getCurrentWeight() {
        int currentWeight = 0;
        for (var passenger: passengers) {
            currentWeight +=  passenger.getWeight();
        }
        return currentWeight;
    }

    public void changeState(ElevatorState state) {
        this.state = state;
    }

    public ElevatorState getState() {
        return state;
    }

    public ElevatorDirection getDirection() {
        return this.direction;
    }

    @Override
    public String toString() {
        return "Elevator{" +
                "id=" + id +
                ", state=" + state +
                ", direction=" + direction +
                ", strategy=" + strategy +
                ", configuration=" + configuration +
                ", passengers=" + passengers +
                ", currentRoute=" + currentRoute +
                ", callingQueue=" + callingQueue +
                ", onFloorTimer=" + onFloorTimer +
                ", onFloorCallback=" + onFloorCallback +
                ", currentFloor=" + currentFloor +
                '}';
    }
}
