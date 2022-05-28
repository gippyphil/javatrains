package train;

import java.util.ArrayDeque;
import java.util.Deque;

import track.Point;
import track.Track;

public class Consist {
    public String name;    
    public Deque<Vehicle> vehicles;
    protected double length = 0;
    protected int id;

    protected static int nextID = 0;

    public Consist (String name, Vehicle firstVehicle) {
        this.name = name;
        vehicles = new ArrayDeque<>();
        vehicles.add(firstVehicle);
        this.id = ++nextID;
    }

    public void addVehicleFront (Vehicle vehicle) {
        vehicles.addFirst(vehicle);
        this.length += vehicle.length;
    }

    public void addVehicleBack (Vehicle vehicle) {
        vehicles.addLast(vehicle);
        this.length += vehicle.length;
    }

    public static Consist createDebugConsist (int vehicleCount, boolean randomizeLengths)
    {
        double len = 5 + (Math.random() * 15);
        double offset = 1 + (Math.random() * (len * 0.2));
        Vehicle firstVehicle = new Vehicle(len, offset, -offset);
        Consist test = new Consist("Debug Consist " + (nextID + 1), firstVehicle);

        for (int i = 1; i < vehicleCount; i++)
        {
            len = 5 + (Math.random() * 15);
            offset = 1 + (Math.random() * (len * 0.2));
            Vehicle nextVehicle = new Vehicle(len, offset, -offset);
            test.addVehicleBack(nextVehicle);
        }

        return test;
    }


    public Track getFrontTrack () {
        return vehicles.getFirst().getFrontTrack();
    }

    /**
     * Retuns the front of the consist, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the front of the front vehicle
     */
    public Point getFrontPoint () {
        // vehicles.getFirst()//
        // TODO Auto-generated method stub
        return null;
    }

    public Track getBackTrack () {
        return vehicles.getLast().getBackTrack();
    }

    /**
     * Retuns the back of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the back of the vehicle
     */
    public Point getBackPoint () {
        // vehicles.getLast()//
        // TODO Auto-generated method stub
        return null;
    }

}
