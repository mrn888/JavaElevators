package com.nulp.logic.entities;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

public class Floor implements IFloor {
    private static int MAX_PASSENGERS = 6;

    int id;
    ArrayList<Passenger> passengers;
    TimerTask passengerGenerator;
    Timer passengerTimer;
    int passengerFrequency;

    Floor(int id, int passengerFrequency, TimerTask passengerGenerator) {
        passengers = new ArrayList<>();
        this.passengerGenerator = passengerGenerator;
        this.passengerFrequency = passengerFrequency;
        this.id = id;

        setPassengerFrequency(this.passengerFrequency);

        passengerTimer = new Timer();
    }

    // TODO: REWORK TO GENERATE RANDOM AMOUNT OF PASSENGERS
    public void generatePassengers() {
        if(passengers.size() + 1 < MAX_PASSENGERS) {
            passengers.add(new Passenger(Passenger.getRandomPassengerWeight()));
        }
    }

    public void setPassengerFrequency(int passengerFrequency) {
        int random =  ThreadLocalRandom.current().nextInt(1, (int)(passengerFrequency/3));
        this.passengerFrequency = passengerFrequency - random;
    }

    public void startPassengerGenerating() {
        passengerTimer.schedule(passengerGenerator, this.passengerFrequency, this.passengerFrequency);

    }
    public void stopPassengerGenerating() {
        passengerTimer.cancel();;
        passengerTimer.purge();
    }

    @Override
    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }
}
