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

public class Elevator implements IElevator, Runnable {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    private int id;
    private ElevatorState state;
    private ElevatorDirection direction;
    private IElevatorStrategy strategy;
    private ElevatorConfiguration configuration;
    private ArrayList<Passenger> passengers;
    private LinkedList<Integer> currentRoute;
    private Timer onFloorTimer;
    private TimerTask onFloorCallback;
    private int currentFloor;
    private Boolean isActive = true;

    Elevator(int id, ElevatorConfiguration configuration) {
        this.id = id;
        this.configuration = configuration;
        this.currentFloor = 0;
        this.direction = ElevatorDirection.UP;
        this.onFloorTimer = new Timer();
        this.state = new StoppedState(this);
        this.strategy = configuration.strategy;
        this.passengers = new ArrayList<>();
        this.currentRoute = new LinkedList<>();
        LOGGER.info("Created elevator : " + this);
    }
    public LinkedList<Integer> getCurrentRoute() {
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
        if (!isActive) {
            this.isActive = true;
            onFloorTimer = new Timer();
            onFloorTimer.schedule(onFloorCallback, 0, configuration.speed * 1000);
        } else {
            onFloorTimer = new Timer();
            onFloorTimer.schedule(onFloorCallback, 0, configuration.speed * 1000);
        }
    }

    public void stopElevatorMovement() {
        if (this.isActive) {
            this.isActive = false;
            onFloorTimer.cancel();
            onFloorTimer.purge();
        }
    }

    public int changeFloor() {
        defineDirection();
        int nextFloor = currentFloor + direction.getValue();
        if(isAbleToChangeFloor(nextFloor) && currentRoute.size() > 0) {
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
                LOGGER.info("Passenger with weight " + passenger.getWeight() + " goes on floor " +  currentFloor);
            }
        }
    }

    @Override
    public void call(int floor) {
        buildRoute();
        if (!currentRoute.contains(floor)) {
            currentRoute.add(floor);
        }
        defineDirection();
        state.onCall();
    }

    public void buildRoute() {
        var route = new LinkedList<Integer>();

        for (var passenger: getPassengers()) {
            route.add(passenger.getFloorTarget());
        }

        currentRoute = route;
    }

    public boolean popFromRoute() {
        Iterator<Integer> i = currentRoute.iterator();
        if(i.hasNext() ) {
            var val = i.next();
            if(val == currentFloor)
                i.remove();
        }
        return currentRoute.isEmpty();
    }

    public void defineDirection() {
        var currentRouteList = new ArrayList<>(currentRoute);
        if(currentRouteList.size() == 0) return;
        if(currentRouteList.get(0) > currentFloor) {
            direction = ElevatorDirection.UP;
        } else if(currentRouteList.get(0) < currentFloor){
            direction = ElevatorDirection.DOWN;
        }
    }

    @Override
    public boolean addPassenger(Passenger passenger) {
        if(isAbleToAddPassenger(passenger)) {
            LOGGER.info("Added passenger: " + passenger.getWeight());
            this.passengers.add(passenger);
            buildRoute();
            return true;
        } else {

        }
        return false;
    }

    @Override
    public ArrayList<Passenger> getPassengers() {
        return passengers;
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
                ", onFloorTimer=" + onFloorTimer +
                ", onFloorCallback=" + onFloorCallback +
                ", currentFloor=" + currentFloor +
                '}';
    }

    @Override
    public void run() {
        LOGGER.info("Running elevator [" +
                Thread.currentThread().getName() + "].");
    }
}
