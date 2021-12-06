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
    int passengerFrequency;

    Floor(int id, int passengerFrequency, TimerTask passengerGenerator) {
        passengers = new ArrayList<>();
        int elevatorsAmount = BuildingConfiguration.getInstance().getElevatorConfiguration().size();
        for(int i = 0 ; i < elevatorsAmount; ++i) {
            passengers.add(new ArrayList<>());
        }
        this.passengerGenerator = passengerGenerator;
        this.passengerFrequency = passengerFrequency;
        this.id = id;

        setPassengerFrequency(this.passengerFrequency);

        passengerTimer = new Timer();
        LOGGER.info("Created floor : " + this);
    }

    public void generatePassengers() {
        passengers.forEach(queue -> {
            if(queue.size() + 1 < MainConfiguration.MAX_PASSENGERS) {
                Passenger passenger = new Passenger(Passenger.getRandomPassengerWeight());
                queue.add(passenger);
                LOGGER.info("Created random passenger : " + passenger);
            }
        });
    }

    public void setPassengerFrequency(int passengerFrequency) {
        int random =  ThreadLocalRandom.current().nextInt(1, (int)(passengerFrequency/3));
        this.passengerFrequency = passengerFrequency - random;
    }

    public void startPassengerGenerating() {
        passengerTimer.schedule(passengerGenerator,2, this.passengerFrequency);

    }
    public void stopPassengerGenerating() {
        passengerTimer.cancel();;
        passengerTimer.purge();
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
