package train;

import java.util.ArrayDeque;
import java.util.Deque;

import path.PathException;
import track.Point;
import track.PointContext;
import track.Track;
import track.TrackEnd;
import track.TrackException;
import windows.Viewport;

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

    public Consist addVehicleFront (Vehicle vehicle) {
        vehicles.addFirst(vehicle);
        this.length += vehicle.length;
        return this;
    }

    public Consist addVehicleBack (Vehicle vehicle) {
        vehicles.addLast(vehicle);
        this.length += vehicle.length;
        return this;
    }

    public static Consist createDebugConsist (int vehicleCount, boolean randomizeLengths)
    {
        double len = randomizeLengths ? 5 + (Math.random() * 15) : 12.0; // 40'
        double offset = randomizeLengths ? 1 + (Math.random() * (len * 0.2)) : 2.0; // 7' from the end
        Vehicle firstVehicle = new Vehicle(len, offset, offset);
        Consist test = new Consist("Debug Consist " + (nextID + 1), firstVehicle);

        for (int i = 1; i < vehicleCount; i++)
        {
            len = randomizeLengths ? 5 + (Math.random() * 15) : 12.0; // 40'
            offset = randomizeLengths ? 1 + (Math.random() * (len * 0.2)) : 2.0; // 7' from the end
            Vehicle nextVehicle = new Vehicle(len, offset, offset);
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
    public PointContext getFrontPoint () {
        return vehicles.getFirst().getFrontPoint();
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
        return vehicles.getLast().getBackPoint();
    }

    public void place (TrackEnd start, double offsetDistance) throws PathException, TrackException
    {
        TrackEnd currentEnd = start;
        double currentOffset = offsetDistance;
        for (Vehicle vehicle : vehicles) {
            vehicle.place(currentEnd, currentOffset);

            PointContext currentEndOfConsist = vehicle.getBackPoint();
            currentEnd = currentEndOfConsist.getEnd();
            currentOffset = currentEndOfConsist.getDistanceFromEnd();
        }
    }

    public void render (Viewport viewport) {
        for (Vehicle vehicle : vehicles)
            vehicle.render(viewport);
    }
}
