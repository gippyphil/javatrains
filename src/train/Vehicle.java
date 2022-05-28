package train;

// TODO is a wagon a subclass?
// TODO loco is definitely a subclass 
// TODO special subclass for articulated vehicles (eg; garrats)?
// TODO special subclass for permanently coupled vehicles (eg: engine and tender)?
public class Vehicle {

    // TODO: physics stuff - weight, etc
    public static final float WIDTH = 3; // metres ... good enough

    // wheels are rotation points with the track, so a single axle or the centre point of a set of fixed axles (eg: a bogie)
    // a steam loco would have two 'wheels' for the front and rear driving wheels - the leading and trailing trucks don't affect
    // the vehicles position
    protected Wheel frontWheel;
    protected Wheel backWheel;
    protected double length;

    protected Vehicle (float length)
    {
        wheels = new ArrayList<>();
    }

    public Track getFrontTrack ();
    public Track 
}
