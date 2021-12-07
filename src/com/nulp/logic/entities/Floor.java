package com.nulp.logic.entities;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

import com.nulp.logic.configuration.*;
import com.nulp.logic.utils.MyLogger;

public class Floor implements IFloor {
    private static final Logger LOGGER = MyLogger.getLOGGER();

    int id;
    ArrayList<ArrayList<Passenger>> passengers;
    TimerTask passengerGenerator;
    Timer passengerTimer;
    Boolean isActive;
    int passengerFrequency;

    Floor(int id, int passengerFrequency) {
        passengers = new ArrayList<>();
        int elevatorsAmount = BuildingConfiguration.getInstance().getElevatorConfiguration().size();
        for(int i = 0 ; i < elevatorsAmount; ++i) {
            passengers.add(new ArrayList<>());
        }
        this.passengerFrequency = passengerFrequency;
        this.id = id;

        setPassengerFrequency(this.passengerFrequency);

        LOGGER.info("Created floor : " + this);
    }

    public void setPassengerGenerator(TimerTask passengerGenerator) {
        this.passengerGenerator = passengerGenerator;
    }

    public void generatePassengers() {
        passengers.forEach(queue -> {
            if(queue.size() + 1 < MainConfiguration.MAX_PASSENGERS) {
                Passenger passenger = new Passenger(Passenger.getRandomPassengerWeight());
                queue.add(passenger);
                LOGGER.info("Created random passenger : " + passenger + " on floor " + id);
            }
        });
    }

    public void setPassengerFrequency(int passengerFrequency) {
        int random =  ThreadLocalRandom.current().nextInt(1, (int)(passengerFrequency/3));
        this.passengerFrequency = passengerFrequency;
    }

    public void startPassengerGenerating() {
        this.isActive = true;
        passengerTimer = new Timer();
        passengerTimer.schedule(passengerGenerator,0, this.passengerFrequency * 1000);

    }

    public void stopPassengerGenerating() {
        if (this.isActive) {
            this.isActive = false;
            passengerTimer.cancel();
            passengerTimer.purge();
        }
    }

    public ArrayList<ArrayList<Passenger>> getPassengers() {
        return passengers;
    }
    @Override
    public ArrayList<Passenger> getPassengers(int elevator) {
        return passengers.get(elevator);
    }

    @Override
    public String toString() {
        return "Floor{" +
                "id=" + id +
                ", passengers=" + passengers +
                ", passengerGenerator=" + passengerGenerator +
                ", passengerTimer=" + passengerTimer +
                ", passengerFrequency=" + passengerFrequency +
                '}';
    }
}
