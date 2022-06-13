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
        double len = randomizeLengths ? 10 + (Math.random() * 10) : 19.0; // 60'
        double offset = len * 0.13;
        Vehicle firstVehicle = new Vehicle(len, offset, offset);
        Consist test = new Consist("Debug Consist " + (nextID + 1), firstVehicle);

        for (int i = 1; i < vehicleCount; i++)
        {
            len = randomizeLengths ? 10 + (Math.random() * 10) : 19.0; // 60'
            offset = len * 0.13;
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
    public Point getFrontPoint () {
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
        double prevOffset = offsetDistance;
        PointContext prevWheel = null;
        Vehicle prevVehicle = null;
        for (Vehicle vehicle : vehicles) {
            if (prevVehicle != null) {
                prevOffset = prevVehicle.distanceToBackWheel + Vehicle.GAP + vehicle.distanceToFrontWheel;
//System.out.format("%01d -> %01d: %1.2f\n", prevVehicle.id, vehicle.id, prevOffset);
            }
            vehicle.place(prevWheel, prevWheel == null ? start : prevWheel.getEnd(), prevOffset);

            prevWheel = vehicle.getBackWheel();
            prevVehicle = vehicle;
        }
    }

    public void render (Viewport viewport) {
        for (Vehicle vehicle : vehicles)
            vehicle.render(viewport);
    }
}
