package train;

import animation.AnimationListener;
import track.Point;
import track.Track;

// TODO is a wagon a subclass?
// TODO loco is definitely a subclass 
// TODO special subclass for articulated vehicles (eg; garrats)?
// TODO special subclass for permanently coupled vehicles (eg: engine and tender)?
public class Vehicle implements AnimationListener {

    // TODO: physics stuff - weight, drag, etc
    public static final float WIDTH = 3; // metres ... good enough

    // wheels are rotation points with the track, so a single axle or the centre point of a set of fixed axles (eg: a bogie)
    // a steam loco would have two 'wheels' for the front and rear driving wheels - the leading and trailing trucks don't affect
    // the vehicles position
    protected Wheel frontWheel;
    protected Wheel backWheel;
    protected double distanceToFrontWheel;
    protected double distanceToBackWheel;
    protected double length;
    protected double speed; // metres per second

    protected Vehicle (double length, double distanceToFrontWheel, double distanceToBackWheel)
    {
        this.length = length;
        this.distanceToFrontWheel = distanceToFrontWheel;
        this.distanceToBackWheel = distanceToBackWheel;
    }

    public Track getFrontTrack () {
        return frontWheel.track;
    }

    /**
     * Retuns the front of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the front of the vehicle
     */
    public Point getFrontPoint () {
        // TODO Auto-generated method stub
        return null;
    }

    public Track getBackTrack () {
        return backWheel.track;
    }

    /**
     * Retuns the back of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the back of the vehicle
     */
    public Point getBackPoint () {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onAnimationTimer() {
        // TODO Auto-generated method stub
        
    }
}
