package com.nulp.logic.entities;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import com.nulp.logic.configuration.*;

public class Floor implements IFloor {
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
    }

    public void generatePassengers() {
        passengers.forEach(queue -> {
            if(queue.size() + 1 < MainConfiguration.MAX_PASSENGERS) {
                queue.add(new Passenger(Passenger.getRandomPassengerWeight()));
            }
//            System.out.println("Passengers on floor[" + id + "] = " + queue.size());
        });
    }

    public void setPassengerFrequency(int passengerFrequency) {
        int random =  ThreadLocalRandom.current().nextInt(1, (int)(passengerFrequency/3));
        this.passengerFrequency = passengerFrequency - random;
    }

    public void startPassengerGenerating() {
        passengerTimer.schedule(passengerGenerator,0, this.passengerFrequency);

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
}
